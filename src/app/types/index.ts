export interface Expense {
  id: string;
  employeeName: string;
  category: 'travel' | 'meal' | 'transport' | 'other';
  amount: number;
  date: string;
  description: string;
  status: 'pending' | 'managerApproved' | 'financeApproved' | 'rejected';
  receipt?: string;
  rejectedBy?: string;
  rejectedByRole?: 'manager' | 'finance';
  rejectionReason?: string;
  approvedBy?: string;
  approvedByRole?: 'manager' | 'finance';
}

export interface Employee {
  id: string;
  name: string;
  email: string;
  department: string;
  // role: 'employee' | 'manager' | 'finance';
  managerId: string;
}

export interface CategoryLimit {
  category: string;
  limit: number;
  spent: number;
}
