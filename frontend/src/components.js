import { useEffect, useRef, useState } from 'react';

export function ErrorMessage({ error }) {
  return error && <p className="error" role="alert">{error}</p>;
}

export function Field({ label, children, ...props }) {
  return <label>{label}{children || <input required {...props} />}</label>;
}

export function Select({ label, name, options, defaultValue = '', empty = 'Selecione', onChange }) {
  return <Field label={label}>
    <select required name={name} defaultValue={defaultValue} onChange={onChange}>
      <option value="">{empty}</option>
      {options.map(([value, caption]) => <option key={value} value={value}>{caption}</option>)}
    </select>
  </Field>;
}

export function ActionForm({ action, children, label = 'Salvar', className = '' }) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  async function submit(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const values = Object.fromEntries(new FormData(form));
    setError('');
    setNotice('');
    setBusy(true);
    try {
      const message = await action(values);
      form.reset();
      setNotice(message || 'Salvo com sucesso.');
    } catch (failure) {
      setError(failure.message);
    } finally {
      setBusy(false);
    }
  }
  return <form onSubmit={submit} className={className}>
    <fieldset disabled={busy}>
      {children}
      <button type="submit">{busy ? 'Aguarde…' : label}</button>
    </fieldset>
    <ErrorMessage error={error} />
    {notice && <p className="success" role="status">{notice}</p>}
  </form>;
}

export function Table({ columns, rows }) {
  if (!rows.length) return <p className="muted">Nenhum registro encontrado.</p>;
  return <div className="table-scroll"><table>
    <thead><tr>{columns.map(column => <th key={column} scope="col">{column}</th>)}</tr></thead>
    <tbody>{rows.map(([id, ...cells]) => <tr key={id}>
      {cells.map((cell, index) => <td key={columns[index]}>{cell ?? '—'}</td>)}
    </tr>)}</tbody>
  </table></div>;
}

export function Drawer({ title, onClose, children }) {
  const dialog = useRef(null);
  useEffect(() => {
    const element = dialog.current;
    const overflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    element.showModal();
    return () => {
      element.close();
      document.body.style.overflow = overflow;
    };
  }, []);
  return <dialog ref={dialog} className="drawer" aria-label={title}
    onCancel={event => { event.preventDefault(); onClose(); }}
    onClick={event => {
      if (event.target === event.currentTarget && event.clientX < event.currentTarget.getBoundingClientRect().left) onClose();
    }}>{children}</dialog>;
}
