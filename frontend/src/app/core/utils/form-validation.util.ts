/** Builds a clear "fill these fields" message for empty required inputs. */
export function missingFieldsMessage(fields: Record<string, boolean>): string | null {
  const missing = Object.entries(fields)
    .filter(([, isMissing]) => isMissing)
    .map(([label]) => label);

  if (missing.length === 0) {
    return null;
  }

  if (missing.length === 1) {
    return `Please fill the required field: ${missing[0]}.`;
  }

  return `Please fill the required fields: ${missing.join(', ')}.`;
}

export function isBlank(value: unknown): boolean {
  if (value === null || value === undefined) {
    return true;
  }
  if (typeof value === 'string') {
    return value.trim().length === 0;
  }
  return false;
}
