export function validDate(value) {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value || '')) return false;
  const parsed = new Date(value + 'T00:00:00Z');
  return Number.isFinite(parsed.getTime()) && parsed.toISOString().slice(0, 10) === value;
}
export function today() {
  return new Intl.DateTimeFormat('sv-SE').format(new Date());
}
export function date(value) {
  return validDate(value) ? value.split('-').reverse().join('/') : '—';
}
export function daysBetween(start, end) {
  return Math.round((Date.parse(end) - Date.parse(start)) / 86400000);
}
