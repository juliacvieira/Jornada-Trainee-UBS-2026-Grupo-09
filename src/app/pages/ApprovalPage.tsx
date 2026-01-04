import { useState } from 'react';
import { CheckCircle, XCircle, Clock } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/button';
import { Textarea } from '../components/ui/textarea';
import { Label } from '../components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '../components/ui/dialog';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import type { TranslationKeys } from '../translations';
import type { Expense } from '../types';

interface ApprovalPageProps {
  t: TranslationKeys;
}

// Mock data - switch for real data integration
const mockPendingExpenses: Expense[] = [
  {
    id: '1',
    employeeName: 'João Santos',
    category: 'travel',
    amount: 3200.00,
    date: '2025-12-20',
    description: 'Airfare to São Paulo - Technology Conference',
    status: 'pending',
  },
  {
    id: '2',
    employeeName: 'Mariana Souza',
    category: 'meal',
    amount: 180.50,
    date: '2025-12-22',
    description: 'Dinner with potential client - Project discussion',
    status: 'pending',
  },
  {
    id: '3',
    employeeName: 'Pedro Lima',
    category: 'transport',
    amount: 95.00,
    date: '2025-12-23',
    description: 'Taxi for urgent meeting with client',
    status: 'pending',
  },
];

const mockApprovedExpenses: Expense[] = [
  {
    id: '4',
    employeeName: 'João Santos',
    category: 'meal',
    amount: 65.00,
    date: '2025-12-15',
    description: 'Executive lunch',
    status: 'financeApproved',
  },
  {
    id: '5',
    employeeName: 'Mariana Souza',
    category: 'transport',
    amount: 45.00,
    date: '2025-12-18',
    description: 'Uber for client office',
    status: 'financeApproved',
  },
];

const mockRejectedExpenses: Expense[] = [
  {
    id: '6',
    employeeName: 'Pedro Lima',
    category: 'meal',
    amount: 250.00,
    date: '2025-12-16',
    description: 'Dinner at the restaurant',
    status: 'rejected',
  },
];

export function ApprovalPage({ t }: ApprovalPageProps) {
  const { user } = useAuth();
  const [pendingExpenses] = useState<Expense[]>(mockPendingExpenses);
  const [approvedExpenses] = useState<Expense[]>(mockApprovedExpenses);
  const [rejectedExpenses] = useState<Expense[]>(mockRejectedExpenses);
  const [selectedExpense, setSelectedExpense] = useState<Expense | null>(null);
  const [actionType, setActionType] = useState<'approve' | 'reject' | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');

  if (!user) return null;

  const handleApprove = (expense: Expense) => {
    setSelectedExpense(expense);
    setActionType('approve');
  };

  const handleReject = (expense: Expense) => {
    setSelectedExpense(expense);
    setActionType('reject');
    setRejectionReason('');
  };

  const handleConfirmAction = () => {
    // Handle approval/rejection
    setSelectedExpense(null);
    setActionType(null);
    setRejectionReason('');
  };

  const handleCancelAction = () => {
    setSelectedExpense(null);
    setActionType(null);
    setRejectionReason('');
  };

  const renderExpenseTable = (expenses: Expense[], showActions: boolean = false) => {
    return (
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left py-3 px-4 text-gray-700">{t.approval.employee}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.date}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.category}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.description}</th>
              <th className="text-right py-3 px-4 text-gray-700">{t.expenses.amount}</th>
              {showActions && (
                <th className="text-center py-3 px-4 text-gray-700">{t.expenses.actions}</th>
              )}
            </tr>
          </thead>
          <tbody>
            {expenses.map((expense) => {
              const categoryKey = expense.category as keyof typeof t.expenses.categories;
              
              return (
                <tr key={expense.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4 text-gray-900">
                    {expense.employeeName}
                  </td>
                  <td className="py-3 px-4 text-gray-700">
                    {new Date(expense.date).toLocaleDateString('pt-BR')}
                  </td>
                  <td className="py-3 px-4">
                    <Badge variant="outline" className="bg-gray-50">
                      {t.expenses.categories[categoryKey]}
                    </Badge>
                  </td>
                  <td className="py-3 px-4 text-gray-700 max-w-xs truncate">
                    {expense.description}
                  </td>
                  <td className="py-3 px-4 text-right text-gray-900">
                    USD {expense.amount.toFixed(2)}
                  </td>
                  {showActions && (
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center gap-2">
                        <Button
                          size="sm"
                          className="bg-green-600 hover:bg-green-700 text-white"
                          onClick={() => handleApprove(expense)}
                        >
                          <CheckCircle className="w-4 h-4 mr-1" />
                          {t.approval.approve}
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          className="border-red-600 text-red-600 hover:bg-red-50"
                          onClick={() => handleReject(expense)}
                        >
                          <XCircle className="w-4 h-4 mr-1" />
                          {t.approval.reject}
                        </Button>
                      </div>
                    </td>
                  )}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="mx-auto px-6 py-8">
          <div>
            <h1 className="text-gray-900 mb-2">{t.approval.title}</h1>
            <p className="text-gray-600">{t.approval.subtitle}</p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto px-6 py-8">
        <Tabs defaultValue="pending" className="space-y-6">
          <TabsList>
            <TabsTrigger value="pending" className="gap-2">
              <Clock className="w-4 h-4" />
              {t.approval.pendingReview}
              <Badge className="ml-2 bg-yellow-100 text-yellow-800 border-yellow-200">
                {pendingExpenses.length}
              </Badge>
            </TabsTrigger>
            <TabsTrigger value="history">
              {t.approval.approvalHistory}
            </TabsTrigger>
          </TabsList>

          <TabsContent value="pending">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Clock className="w-5 h-5 text-[#E60000]" />
                  {t.approval.pendingReview}
                </CardTitle>
                <CardDescription>
                  {pendingExpenses.length} {t.approval.pending}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {renderExpenseTable(pendingExpenses, true)}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="history" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-green-600" />
                  {t.approval.approved}
                </CardTitle>
                <CardDescription>
                  {approvedExpenses.length} {t.approval.checkedExpenses}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {renderExpenseTable(approvedExpenses)}
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <XCircle className="w-5 h-5 text-red-600" />
                  {t.approval.rejected}
                </CardTitle>
                <CardDescription>
                  {rejectedExpenses.length} {t.approval.notApproved}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {renderExpenseTable(rejectedExpenses)}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      {/* Approval/Rejection Dialog */}
      <Dialog open={!!selectedExpense} onOpenChange={(open) => !open && handleCancelAction()}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>
              {actionType === 'approve' ? t.approval.confirmApprove : t.approval.confirmReject}
            </DialogTitle>
            <DialogDescription>
              {selectedExpense && (
                <div className="mt-4 space-y-2 text-sm">
                  <p><strong>{t.approval.employee}:</strong> {selectedExpense.employeeName}</p>
                  <p><strong>{t.expenses.amount}:</strong> USD {selectedExpense.amount.toFixed(2)}</p>
                  <p><strong>{t.expenses.description}:</strong> {selectedExpense.description}</p>
                </div>
              )}
            </DialogDescription>
          </DialogHeader>

          {actionType === 'reject' && (
            <div className="space-y-2">
              <Label htmlFor="reason">{t.approval.reason}</Label>
              <Textarea
                id="reason"
                placeholder={t.approval.reasonPlaceholder}
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                rows={4}
                required
              />
            </div>
          )}

          <div className="flex gap-2">
            <Button
              variant="outline"
              className="flex-1"
              onClick={handleCancelAction}
            >
              {t.expenses.cancel}
            </Button>
            <Button
              className={`flex-1 ${
                actionType === 'approve'
                  ? 'bg-green-600 hover:bg-green-700'
                  : 'bg-red-600 hover:bg-red-700'
              } text-white`}
              onClick={handleConfirmAction}
              disabled={actionType === 'reject' && !rejectionReason.trim()}
            >
              {actionType === 'approve' ? t.approval.approve : t.approval.reject}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
