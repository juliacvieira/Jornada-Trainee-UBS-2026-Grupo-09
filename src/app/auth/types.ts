export type UserRole = "employee" | "manager" | "finance";

export interface User {
  email: string;
  role: UserRole;
  token?: string;
}
