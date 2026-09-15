import { localDateTimeValue, toApiDateTime } from './datetime';

describe('datetime helpers', () => {
  it('pads datetime-local values', () => {
    const value = localDateTimeValue(0);
    expect(value).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/);
  });

  it('adds seconds for the API', () => {
    expect(toApiDateTime('2026-09-16T10:30')).toBe('2026-09-16T10:30:00');
    expect(toApiDateTime('2026-09-16T10:30:00')).toBe('2026-09-16T10:30:00');
  });
});
