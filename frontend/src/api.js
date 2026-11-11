const base = (process.env.REACT_APP_API_URL || 'http://localhost:8080').replace(/\/$/, '');

export async function request(path, { token, method = 'GET', body, signal } = {}) {
  let response;
  try {
    response = await fetch(base + path, {
      method, signal,
      headers: {
        ...(body !== undefined && { 'Content-Type': 'application/json' }),
        ...(token && { Authorization: 'Bearer ' + token }),
      },
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch (error) {
    if (error.name === 'AbortError') throw error;
    throw new Error('Não foi possível conectar à API. Confira se o backend está em execução.');
  }
  const payload = await response.json().catch(() => null);
  if (!response.ok) {
    const validation = response.status === 400 && payload && !payload.status
      ? Object.values(payload).filter(value => typeof value === 'string').join(' ')
      : '';
    const error = new Error(payload?.message || validation || (response.status === 401
      ? 'Credenciais inválidas ou sessão expirada.'
      : 'A API retornou o erro ' + response.status + '.'));
    error.status = response.status;
    throw error;
  }
  return payload;
}

export async function list(api, path, signal) {
  const page = number => api(path + '?page=' + number + '&size=100&sort=id,asc', { signal });
  const first = await page(0);
  const items = [...first.content];
  for (let number = 1; number < first.totalPages; number++) {
    items.push(...(await page(number)).content);
  }
  return items;
}
