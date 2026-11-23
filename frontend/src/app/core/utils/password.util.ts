/** Mirrors backend PasswordPolicy for instant UI feedback */
export function isStrongPassword(password: string): boolean {
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#^()_\-+={}\[\]|:;"'<>,./~`]).{8,}$/.test(
    password
  );
}

export const STRONG_PASSWORD_HINT =
  'Min 8 chars with upper, lower, number, and special character (e.g. Manager@12345).';
