import { apiFetch } from "./apiClient";

export interface EmployeeResponse {
  id: string;
  name: string;
  email: string;
  position: string;
  departmentId: string | null;
  managerId: string | null;
}

export interface CreateEmployeeRequest {
  name: string;
  email: string;
  position: string;
  departmentId?: string | null;
  managerId?: string | null;
}

export interface UpdateEmployeeRequest {
  name: string;
  email: string;
  position: string;
  departmentId?: string | null;
  managerId?: string | null;
}

export function listEmployees() {
  return apiFetch<EmployeeResponse[]>("/employees");
}

export function getEmployee(id: string) {
  return apiFetch<EmployeeResponse>(`/employees/${id}`);
}

export function createEmployee(body: CreateEmployeeRequest) {
  return apiFetch<EmployeeResponse>("/employees", {
    method: "POST",
    body: JSON.stringify(body),
  });
}

export function updateEmployee(id: string, body: UpdateEmployeeRequest) {
  return apiFetch<EmployeeResponse>(`/employees/${id}`, {
    method: "PATCH",
    body: JSON.stringify(body),
  });
}

export function deleteEmployee(id: string) {
  return apiFetch<void>(`/employees/${id}`, {
    method: "DELETE",
  });
}
