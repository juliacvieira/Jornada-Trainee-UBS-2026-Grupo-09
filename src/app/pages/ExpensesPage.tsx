import { useState } from 'react';
import { Plus, FileText, TrendingUp, Download, Edit, Search, ChevronDown, Eye } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Textarea } from '../components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '../components/ui/dialog';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { Progress } from '../components/ui/progress';
import type { TranslationKeys } from '../translations';
import type { Expense, CategoryLimit } from '../types';

interface ExpensesPageProps {
  t: TranslationKeys;
}

// Mock data - switch for real data integration
const mockExpenses: Expense[] = [
  {
    id: '1',
    employeeName: 'João Silva',
    category: 'travel',
    amount: 2500.00,
    date: '2025-12-20',
    description: 'Trip to conference in São Paulo',
    status: 'managerApproved',
    approvedBy: 'Maria Santos',
    approvedByRole: 'manager',
  },
  {
    id: '2',
    employeeName: 'João Silva',
    category: 'meal',
    amount: 85.50,
    date: '2025-12-22',
    description: 'Lunch with client',
    status: 'pending',
  },
  {
    id: '3',
    employeeName: 'João Silva',
    category: 'transport',
    amount: 45.00,
    date: '2025-12-23',
    description: 'Uber for meeting',
    status: 'managerApproved',
    approvedBy: 'Maria Santos',
    approvedByRole: 'manager',
  },
  {
    id: '4',
    employeeName: 'João Silva',
    category: 'meal',
    amount: 120.00,
    date: '2025-12-24',
    description: 'Dinner with the team',
    status: 'rejected',
    rejectedBy: 'Carlos Oliveira',
    rejectedByRole: 'manager',
    rejectionReason: 'Value exceed the limit. Internal policy allows maximum of USD 80 per person.',
  },
  {
    id: '5',
    employeeName: 'João Silva',
    category: 'travel',
    amount: 3500.00,
    date: '2025-12-18',
    description: 'International flight',
    status: 'rejected',
    rejectedBy: 'Julia Ferreira',
    rejectedByRole: 'finance',
    rejectionReason: 'International travels are not supported during this period',
  },
];

const mockLimits: CategoryLimit[] = [
  { category: 'travel', limit: 5000, spent: 2500 },
  { category: 'meal', limit: 1000, spent: 205.50 },
  { category: 'transport', limit: 500, spent: 45 },
  { category: 'other', limit: 1000, spent: 0 },
];

export function ExpensesPage({ t }: ExpensesPageProps) {
  const { user } = useAuth();
  const [expenses] = useState<Expense[]>(mockExpenses);
  const [limits] = useState<CategoryLimit[]>(mockLimits);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isDetailsDialogOpen, setIsDetailsDialogOpen] = useState(false);
  const [selectedExpenseForDetails, setSelectedExpenseForDetails] = useState<Expense | null>(null);
  const [receiptFile, setReceiptFile] = useState<File | null>(null);
  const [editingExpense, setEditingExpense] = useState<Expense | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [newExpense, setNewExpense] = useState({
    category: '',
    amount: '',
    date: '',
    description: '',
  });

  if (!user) return null;

  const handleSubmitExpense = (e: React.FormEvent) => {
    e.preventDefault();
    // Handle expense submission including receipt file
    console.log('Receipt file:', receiptFile);
    setIsDialogOpen(false);
    setNewExpense({ category: '', amount: '', date: '', description: '' });
    setReceiptFile(null);
    setEditingExpense(null);
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setReceiptFile(e.target.files[0]);
    }
  };

  const handleDownloadReceipt = (expenseId: string) => {
    // Mock download - in real app, download from server
    console.log('Downloading receipt for expense:', expenseId);
    alert('Download started');
  };

  const handleEditExpense = (expense: Expense) => {
    setEditingExpense(expense);
    setNewExpense({
      category: expense.category,
      amount: expense.amount.toString(),
      date: expense.date,
      description: expense.description,
    });
    setIsDialogOpen(true);
  };

  const handleViewDetails = (expense: Expense) => {
    setSelectedExpenseForDetails(expense);
    setIsDetailsDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingExpense(null);
    setNewExpense({ category: '', amount: '', date: '', description: '' });
    setReceiptFile(null);
  };

  // Filter expenses based on search and status
  const filteredExpenses = expenses.filter((expense) => {
    const matchesSearch = 
      expense.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.amount.toString().includes(searchQuery);
    
    const matchesStatus = statusFilter === 'all' || expense.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  const getStatusColor = (status: Expense['status']) => {
    switch (status) {
      case 'managerApproved':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'financeApproved':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'rejected':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="mx-auto px-6 py-8">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-gray-900 mb-2">{t.expenses.title}</h1>
              <p className="text-gray-600">{t.expenses.subtitle}</p>
            </div>
            <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
              <DialogTrigger asChild>
                <Button className="bg-[#E60000] hover:bg-[#CC0000] text-white">
                  <Plus className="w-4 h-4 mr-2" />
                  {t.expenses.newExpense}
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-md">
                <DialogHeader>
                  <DialogTitle>{t.expenses.newExpense}</DialogTitle>
                  <DialogDescription>
                    {t.expenses.subtitle}
                  </DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmitExpense} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="category">{t.expenses.category}</Label>
                    <Select
                      value={newExpense.category}
                      onValueChange={(value) => setNewExpense({ ...newExpense, category: value })}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder={t.expenses.selectCategory} />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="travel">{t.expenses.categories.travel}</SelectItem>
                        <SelectItem value="meal">{t.expenses.categories.meal}</SelectItem>
                        <SelectItem value="transport">{t.expenses.categories.transport}</SelectItem>
                        <SelectItem value="other">{t.expenses.categories.other}</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="amount">{t.expenses.amount}</Label>
                    <Input
                      id="amount"
                      type="number"
                      step="0.01"
                      placeholder="0.00"
                      value={newExpense.amount}
                      onChange={(e) => setNewExpense({ ...newExpense, amount: e.target.value })}
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="date">{t.expenses.date}</Label>
                    <Input
                      id="date"
                      type="date"
                      value={newExpense.date}
                      onChange={(e) => setNewExpense({ ...newExpense, date: e.target.value })}
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="description">{t.expenses.description}</Label>
                    <Textarea
                      id="description"
                      placeholder={t.expenses.descriptionPlaceholder}
                      value={newExpense.description}
                      onChange={(e) => setNewExpense({ ...newExpense, description: e.target.value })}
                      rows={3}
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="receipt">{t.expenses.receipt}</Label>
                    <Input
                      id="receipt"
                      type="file"
                      accept="image/*,application/pdf"
                      onChange={handleFileChange}
                      required={!editingExpense}
                    />
                  </div>

                  <div className="flex gap-2 pt-4">
                    <Button
                      type="button"
                      variant="outline"
                      className="flex-1"
                      onClick={handleCloseDialog}
                    >
                      {t.expenses.cancel}
                    </Button>
                    <Button
                      type="submit"
                      className="flex-1 bg-[#E60000] hover:bg-[#CC0000] text-white"
                    >
                      {t.expenses.submit}
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
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          {/* Category Limits */}
          <Card className="lg:col-span-3">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-[#E60000]" />
                {t.expenses.limits}
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                {limits.map((limit) => {
                  const percentage = (limit.spent / limit.limit) * 100;
                  const categoryKey = limit.category as keyof typeof t.expenses.categories;
                  
                  return (
                    <div key={limit.category} className="space-y-2">
                      <div className="flex items-center justify-between">
                        <span className="text-sm text-gray-700">
                          {t.expenses.categories[categoryKey]}
                        </span>
                        <span className="text-sm text-gray-500">
                          {percentage.toFixed(0)}%
                        </span>
                      </div>
                      <Progress value={percentage} className="h-2" />
                      <div className="flex items-center justify-between text-xs text-gray-600">
                        <span>
                          {t.expenses.spent}: USD {limit.spent.toFixed(2)}
                        </span>
                        <span>
                          {t.expenses.limit}: USD {limit.limit.toFixed(2)}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </CardContent>
          </Card>

          {/* Expense History */}
          <Card className="lg:col-span-3">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <FileText className="w-5 h-5 text-[#E60000]" />
                {t.expenses.history}
              </CardTitle>
              <CardDescription>
                {expenses.length} {t.expenses.historySubtitle}
              </CardDescription>
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
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                  <SelectTrigger className="w-[200px]">
                    <SelectValue placeholder={t.expenses.filterByStatus} />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">{t.expenses.allStatus}</SelectItem>
                    <SelectItem value="pending">{t.expenses.statusLabels.pending}</SelectItem>
                    <SelectItem value="managerApproved">{t.expenses.statusLabels.managerApproved}</SelectItem>
                    <SelectItem value="financeApproved">{t.expenses.statusLabels.financeApproved}</SelectItem>
                    <SelectItem value="rejected">{t.expenses.statusLabels.rejected}</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardHeader>
            <CardContent>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b border-gray-200">
                      <th className="text-left py-3 px-4 text-gray-700">{t.expenses.date}</th>
                      <th className="text-left py-3 px-4 text-gray-700">{t.expenses.category}</th>
                      <th className="text-left py-3 px-4 text-gray-700">{t.expenses.description}</th>
                      <th className="text-right py-3 px-4 text-gray-700">{t.expenses.amount}</th>
                      <th 
                        className="text-center py-3 px-4 text-gray-700">{t.expenses.status}
                          <DropdownMenu>
                          <DropdownMenuTrigger className="p-2">
                            <ChevronDown className="w-4 h-4" />
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="center">
                            <DropdownMenuItem onClick={() => setStatusFilter('all')}>
                              {t.expenses.allStatus}
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => setStatusFilter('pending')}>
                              {t.expenses.statusLabels.pending}
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => setStatusFilter('managerApproved')}>
                              {t.expenses.statusLabels.managerApproved}
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => setStatusFilter('financeApproved')}>
                              {t.expenses.statusLabels.financeApproved}
                            </DropdownMenuItem>
                            <DropdownMenuItem onClick={() => setStatusFilter('rejected')}>
                              {t.expenses.statusLabels.rejected}
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </th>
                      <th className="text-center py-3 px-4 text-gray-700">{t.expenses.receipt}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredExpenses.map((expense) => {
                      const categoryKey = expense.category as keyof typeof t.expenses.categories;
                      const statusKey = expense.status as keyof typeof t.expenses.statusLabels;
                      
                      return (
                        <tr key={expense.id} className="border-b border-gray-100 hover:bg-gray-50">
                          <td className="py-3 px-4 text-gray-900">
                            {new Date(expense.date).toLocaleDateString('pt-BR')}
                          </td>
                          <td className="py-3 px-4">
                            <Badge variant="outline" className="bg-gray-50">
                              {t.expenses.categories[categoryKey]}
                            </Badge>
                          </td>
                          <td className="py-3 px-4 text-gray-700">
                            {expense.description}
                          </td>
                          <td className="py-3 px-4 text-right text-gray-900">
                            USD {expense.amount.toFixed(2)}
                          </td>
                          <td className="py-3 px-4">
                            <div className="flex items-center justify-center gap-2">
                              <Badge className={getStatusColor(expense.status)}>
                                {t.expenses.statusLabels[statusKey]}
                              </Badge>
                              <button
                                onClick={() => handleViewDetails(expense)}
                                className="p-1 hover:bg-gray-200 rounded transition-colors"
                                title={t.expenses.viewDetails}
                              >
                                <Eye className="w-4 h-4 text-gray-600" />
                              </button>
                              <button
                                onClick={() => handleEditExpense(expense)}
                                className="p-1 hover:bg-gray-200 rounded transition-colors"
                                title={t.common.edit}
                              >
                                <Edit className="w-4 h-4 text-gray-600" />
                              </button>
                            </div>
                          </td>
                          <td className="py-3 px-4 text-center">
                            <Button
                              size="sm"
                              className="bg-[#E60000] hover:bg-[#CC0000] text-white"
                              onClick={() => handleDownloadReceipt(expense.id)}
                            >
                              <Download className="w-4 h-4 mr-2" />
                              {t.expenses.downloadReceipt}
                            </Button>
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
      {/* Expense Details Dialog */}
      <Dialog open={isDetailsDialogOpen} onOpenChange={setIsDetailsDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{t.expenses.expenseDetails}</DialogTitle>
            <DialogDescription>
              {t.expenses.detailsSubtitle}
            </DialogDescription>
          </DialogHeader>
          {selectedExpenseForDetails && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label className="text-gray-600 text-sm">{t.expenses.date}</Label>
                  <p className="text-gray-900 mt-1">
                    {new Date(selectedExpenseForDetails.date).toLocaleDateString('pt-BR')}
                  </p>
                </div>
                <div>
                  <Label className="text-gray-600 text-sm">{t.expenses.category}</Label>
                  <p className="text-gray-900 mt-1">
                    <Badge variant="outline" className="bg-gray-50">
                      {t.expenses.categories[selectedExpenseForDetails.category as keyof typeof t.expenses.categories]}
                    </Badge>
                  </p>
                </div>
              </div>

              <div>
                <Label className="text-gray-600 text-sm">{t.expenses.amount}</Label>
                <p className="text-gray-900 text-xl mt-1">
                  USD {selectedExpenseForDetails.amount.toFixed(2)}
                </p>
              </div>

              <div>
                <Label className="text-gray-600 text-sm">{t.expenses.description}</Label>
                <p className="text-gray-900 mt-1">
                  {selectedExpenseForDetails.description}
                </p>
              </div>

              <div>
                <Label className="text-gray-600 text-sm">{t.expenses.status}</Label>
                <p className="mt-1">
                  <Badge className={getStatusColor(selectedExpenseForDetails.status)}>
                    {t.expenses.statusLabels[selectedExpenseForDetails.status as keyof typeof t.expenses.statusLabels]}
                  </Badge>
                </p>
              </div>

              {/* Show approval/rejection details */}
              {selectedExpenseForDetails.status === 'managerApproved' || selectedExpenseForDetails.status === 'financeApproved' 
                && selectedExpenseForDetails.approvedBy && (
                <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                  <Label className="text-green-800 text-sm font-semibold">
                    {t.expenses.approvalDetails}
                  </Label>
                  <div className="mt-2 space-y-1">
                    <p className="text-sm text-green-900">
                      <strong>{t.expenses.approvedBy}:</strong> {selectedExpenseForDetails.approvedBy}
                    </p>
                    <p className="text-sm text-green-900">
                      <strong>{t.expenses.approverRole}:</strong>{' '}
                      {selectedExpenseForDetails.approvedByRole === 'manager' 
                        ? t.employees.roles.manager 
                        : t.employees.roles.finance}
                    </p>
                  </div>
                </div>
              )}

              {selectedExpenseForDetails.status === 'rejected' && selectedExpenseForDetails.rejectedBy && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                  <Label className="text-red-800 text-sm font-semibold">
                    {t.expenses.rejectionDetails}
                  </Label>
                  <div className="mt-2 space-y-2">
                    <p className="text-sm text-red-900">
                      <strong>{t.expenses.rejectedBy}:</strong> {selectedExpenseForDetails.rejectedBy}
                    </p>
                    <p className="text-sm text-red-900">
                      <strong>{t.expenses.rejectorRole}:</strong>{' '}
                      {selectedExpenseForDetails.rejectedByRole === 'manager' 
                        ? t.employees.roles.manager 
                        : t.employees.roles.finance}
                    </p>
                    {selectedExpenseForDetails.rejectionReason && (
                      <div className="mt-2">
                        <strong className="text-sm text-red-900">{t.expenses.rejectionReason}:</strong>
                        <p className="text-sm text-red-900 mt-1 bg-white p-2 rounded border border-red-200">
                          {selectedExpenseForDetails.rejectionReason}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {selectedExpenseForDetails.status === 'pending' && (
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <Label className="text-yellow-800 text-sm font-semibold">
                    {t.expenses.pendingDetails}
                  </Label>
                  <p className="text-sm text-yellow-900 mt-2">
                    {t.expenses.pendingMessage}
                  </p>
                </div>
              )}

              <div className="flex justify-end pt-4">
                <Button
                  onClick={() => setIsDetailsDialogOpen(false)}
                  className="bg-[#E60000] hover:bg-[#CC0000] text-white"
                >
                  {t.common.close}
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}