export type UserRole = "EMPLOYEE" | "MANAGER" | "FINANCE";

export interface User {
  email: string;
  role: UserRole;
  token?: string;
}
