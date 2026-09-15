export function localDateTimeValue(hoursFromNow: number): string {
  const d = new Date(Date.now() + hoursFromNow * 3600_000);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export function toApiDateTime(value: string): string {
  if (!value) {
    return value;
  }
  return value.length === 16 ? `${value}:00` : value;
}
