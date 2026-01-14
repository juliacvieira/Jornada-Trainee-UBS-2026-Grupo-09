export type UserRole = "EMPLOYEE" | "MANAGER" | "FINANCE"

export interface User {
  id?: string
  email: string
  role: UserRole
  token?: string
}