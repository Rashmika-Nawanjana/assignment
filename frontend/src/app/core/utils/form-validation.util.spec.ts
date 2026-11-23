import { isBlank, missingFieldsMessage } from './form-validation.util';

describe('form-validation.util', () => {
  it('detects blank values', () => {
    expect(isBlank('')).toBeTrue();
    expect(isBlank('  ')).toBeTrue();
    expect(isBlank(null)).toBeTrue();
    expect(isBlank('ok')).toBeFalse();
  });

  it('builds a single-field message', () => {
    expect(missingFieldsMessage({ 'Check-in date': true, Nights: false })).toBe(
      'Please fill the required field: Check-in date.'
    );
  });

  it('builds a multi-field message', () => {
    expect(missingFieldsMessage({ 'Guest full name': true, 'NIC / ID number': true })).toBe(
      'Please fill the required fields: Guest full name, NIC / ID number.'
    );
  });

  it('returns null when nothing is missing', () => {
    expect(missingFieldsMessage({ Username: false })).toBeNull();
  });
});
