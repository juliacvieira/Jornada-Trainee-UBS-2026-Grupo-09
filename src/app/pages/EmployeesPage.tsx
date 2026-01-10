import { useState } from "react";
import { Plus, Users, Edit, Trash2, Search } from "lucide-react";
import { Checkbox } from "../components/ui/checkbox";
import { useAuth } from '../hooks/useAuth';
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import type { TranslationKeys } from "../translations";
import type { Employee } from "../types";

interface EmployeesPageProps {
  t: TranslationKeys;
}

// Mock data - switch for real data integration
const mockEmployees: Employee[] = [
  {
    id: "1",
    name: "Ana Costa",
    email: "ana.costa@ubs.com",
    department: "sales",
    managerId: "2",
  },
  {
    id: "2",
    name: "Carlos Silva",
    email: "carlos.silva@ubs.com",
    department: "it",
    managerId: "1",
  },
  {
    id: "3",
    name: "João Santos",
    email: "joao.santos@ubs.com",
    department: "sales",
    managerId: "1",
  },
  {
    id: "4",
    name: "Mariana Souza",
    email: "mariana.souza@ubs.com",
    department: "marketing",
    managerId: "2",
  },
  {
    id: "5",
    name: "Pedro Lima",
    email: "pedro.lima@ubs.com",
    department: "it",
    managerId: "2",
  },
  {
    id: "6",
    name: "Julia Oliveira",
    email: "julia.oliveira@ubs.com",
    department: "finance",
    managerId: "1",
  },
];

export function EmployeesPage({ t }: EmployeesPageProps) {
  const { user } = useAuth();
  const [employees, setEmployees] = useState<Employee[]>(mockEmployees);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [editingEmployee, setEditingEmployee] = useState<Employee | null>(null);
  const [newEmployee, setNewEmployee] = useState({
    name: "",
    email: "",
    department: "",
    managerId: "",
  });

  if (!user) return null;

  const handleSubmitEmployee = (e: React.FormEvent) => {
    e.preventDefault();

    // Create new employee object
    const employee: Employee = {
      id: String(Date.now()), // Generate unique ID
      name: newEmployee.name,
      email: newEmployee.email,
      department: newEmployee.department,
      managerId: newEmployee.managerId,
    };

    if (editingEmployee) {
      // Update existing employee
      setEmployees((prev) =>
        prev.map((emp) =>
          emp.id === editingEmployee.id ? { ...employee, id: editingEmployee.id } : emp
        )
      );
    } else {
      // Add new employee to the list
      setEmployees((prev) => [employee, ...prev]);
    }

    // Reset form
    setIsDialogOpen(false);
    setNewEmployee({
      name: "",
      email: "",
      department: "",
      managerId: "",
    });
    setEditingEmployee(null);
  };

  const handleEditEmployee = (employee: Employee) => {
    setEditingEmployee(employee);
    setNewEmployee({
      name: employee.name,
      email: employee.email,
      department: employee.department,
      managerId: employee.managerId,
    });
    setIsDialogOpen(true);
  };

  const handleToggleSelect = (id: string) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const handleDeleteSelected = () => {
    if (selectedIds.length > 0) {
      console.log("Deleting employees:", selectedIds);
      setEmployees((prev) => prev.filter((emp) => !selectedIds.includes(emp.id)));
      setSelectedIds([]);
    }
  };

  const handleDeleteEmployee = (id: string) => {
    // Remove the employee with the given id
    setEmployees((prev) => prev.filter((emp) => emp.id !== id));
    // Also ensure it's removed from the selectedIds if present
    setSelectedIds((prev) => prev.filter((selectedId) => selectedId !== id));
  };

  // Filter employees based on search
  const filteredEmployees = employees.filter(
    (employee) =>
      employee.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      employee.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      employee.department.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // const managers = employees.filter(
  //   (emp) => emp.role === "manager",
  // );

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="mx-auto px-6 py-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-gray-900 mb-2">
                {t.employees.title}
              </h1>
              <p className="text-gray-600">
                {t.employees.subtitle}
              </p>
            </div>
            <Dialog
              open={isDialogOpen}
              onOpenChange={setIsDialogOpen}
            >
              <DialogTrigger asChild>
                <Button className="bg-[#E60000] hover:bg-[#CC0000] text-white">
                  <Plus className="w-4 h-4 mr-2" />
                  {t.employees.newEmployee}
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-md">
                <DialogHeader>
                  <DialogTitle>
                    {t.employees.newEmployee}
                  </DialogTitle>
                  <DialogDescription>
                    {t.employees.subtitle}
                  </DialogDescription>
                </DialogHeader>
                <form
                  onSubmit={handleSubmitEmployee}
                  className="space-y-4"
                >
                  <div className="space-y-2">
                    <Label htmlFor="name">
                      {t.employees.name}
                    </Label>
                    <Input
                      id="name"
                      type="text"
                      placeholder={t.employees.namePlaceholder}
                      value={newEmployee.name}
                      onChange={(e) =>
                        setNewEmployee({
                          ...newEmployee,
                          name: e.target.value,
                        })
                      }
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="email">
                      {t.employees.email}
                    </Label>
                    <Input
                      id="email"
                      type="email"
                      placeholder={t.employees.emailPlaceholder}
                      value={newEmployee.email}
                      onChange={(e) =>
                        setNewEmployee({
                          ...newEmployee,
                          email: e.target.value,
                        })
                      }
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="department">
                      {t.employees.department}
                    </Label>
                    <Select
                      value={newEmployee.department}
                      onValueChange={(value) =>
                        setNewEmployee({
                          ...newEmployee,
                          department: value,
                        })
                      }
                    >
                      <SelectTrigger>
                        <SelectValue
                          placeholder={
                            t.employees.selectDepartment
                          }
                        />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="it">
                          {t.employees.departments.it}
                        </SelectItem>
                        <SelectItem value="sales">
                          {t.employees.departments.sales}
                        </SelectItem>
                        <SelectItem value="marketing">
                          {t.employees.departments.marketing}
                        </SelectItem>
                        <SelectItem value="hr">
                          {t.employees.departments.hr}
                        </SelectItem>
                        <SelectItem value="finance">
                          {t.employees.departments.finance}
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="flex gap-2 pt-4">
                    <Button
                      type="button"
                      variant="outline"
                      className="flex-1"
                      onClick={() => setIsDialogOpen(false)}
                    >
                      {t.expenses.cancel}
                    </Button>
                    <Button
                      type="submit"
                      className="flex-1 bg-[#E60000] hover:bg-[#CC0000] text-white"
                    >
                      {t.common.save}
                    </Button>
                  </div>
                </form>
              </DialogContent>
            </Dialog>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto px-6 py-8">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="w-5 h-5 text-[#E60000]" />
              {t.employees.list}
            </CardTitle>
            <CardDescription>
              {filteredEmployees.length} {t.employees.registeredList}
            </CardDescription>
            {/* Search and Actions Bar */}
            <div className="flex gap-4 mt-4">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder={t.common.search}
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
              {selectedIds.length > 0 && (
                <Button
                  variant="outline"
                  className="border-red-600 text-red-600 hover:bg-red-50"
                  onClick={handleDeleteSelected}
                >
                  <Trash2 className="w-4 h-4 mr-2" />
                  {t.common.remove} {selectedIds.length} {t.common.select}
                </Button>
              )}
            </div>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 w-12"></th>
                    <th className="text-left py-3 px-4 text-gray-700">
                      {t.employees.name}
                    </th>
                    <th className="text-left py-3 px-4 text-gray-700">
                      {t.employees.email}
                    </th>
                    <th className="text-left py-3 px-4 text-gray-700">
                      {t.employees.department}
                    </th>
                    <th className="text-center py-3 px-4 text-gray-700">
                      {t.expenses.actions}
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredEmployees.map((employee) => {
                    const departmentKey =
                      employee.department as keyof typeof t.employees.departments;

                    return (
                      <tr
                        key={employee.id}
                        className="border-b border-gray-100 hover:bg-gray-50"
                      >
                        <td className="py-3 px-4">
                          <Checkbox
                            checked={selectedIds.includes(employee.id)}
                            onCheckedChange={() => handleToggleSelect(employee.id)}
                          />
                        </td>
                        <td className="py-3 px-4 text-gray-900">
                          {employee.name}
                        </td>
                        <td className="py-3 px-4 text-gray-700">
                          {employee.email}
                        </td>
                        <td className="py-3 px-4">
                          <Badge
                            variant="outline"
                            className="bg-gray-50"
                          >
                            {
                              t.employees.departments[
                                departmentKey
                              ]
                            }
                          </Badge>
                        </td>
                        <td className="py-3 px-4">
                          <div className="flex items-center justify-center gap-2">
                            <Button
                              onClick={() => handleEditEmployee(employee)}
                              variant="ghost"
                              size="icon"
                              className="h-8 w-8"
                              title={t.common.edit}
                            >
                              <Edit className="w-4 h-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              className="h-8 w-8 text-red-600 hover:text-red-700"
                              onClick={() => handleDeleteEmployee(employee.id)}
                              title={t.common.delete}
                            >
                              <Trash2 className="w-4 h-4" />
                            </Button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}