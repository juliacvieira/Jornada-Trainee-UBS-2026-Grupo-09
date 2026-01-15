import { useState, useEffect } from 'react';
import { CheckCircle, XCircle, Clock, FileText, Search, Download } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';
import { Button } from '../components/ui/button';
import { Textarea } from '../components/ui/textarea';
import { Label } from '../components/ui/label';
import { Input } from '../components/ui/input';
import { Checkbox } from '../components/ui/checkbox';

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
import type { TranslationKeys, Language } from '../translations';
import type { Expense } from '../types';
import { formatDate } from '../lib/date';

interface ApprovalPageProps {
  t: TranslationKeys;
  language: Language;
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
    receipt: 'https://via.placeholder.com/800x1000.png?text=Comprovante+1',
  },
  {
    id: '2',
    employeeName: 'Mariana Souza',
    category: 'meal',
    amount: 180.50,
    date: '2025-12-22',
    description: 'Dinner with potential client - Project discussion',
    status: 'pending',
    receipt: 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf',
  },
  {
    id: '3',
    employeeName: 'Pedro Lima',
    category: 'transport',
    amount: 95.00,
    date: '2025-12-23',
    description: 'Taxi for urgent meeting with client',
    status: 'pending',
    receipt: 'https://via.placeholder.com/800x600.png?text=Comprovante+3',
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
    receipt: 'https://via.placeholder.com/800x600.png?text=Comprovante+4',
  },
  {
    id: '5',
    employeeName: 'Mariana Souza',
    category: 'transport',
    amount: 45.00,
    date: '2025-12-18',
    description: 'Uber for client office',
    status: 'financeApproved',
    receipt: 'https://via.placeholder.com/800x600.png?text=Comprovante+5',
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
    receipt: 'https://via.placeholder.com/800x600.png?text=Comprovante+6',
  },
];

export function ApprovalPage({ t, language }: ApprovalPageProps) {
  const { user } = useAuth();
  const [pendingExpenses, setPendingExpenses] = useState<Expense[]>(mockPendingExpenses);
  const [approvedExpenses] = useState<Expense[]>(mockApprovedExpenses);
  const [rejectedExpenses] = useState<Expense[]>(mockRejectedExpenses);

  useEffect(() => {
    const onCanceled = (e: Event) => {
      // CustomEvent detail contains the canceled expense id
      const custom = e as CustomEvent<string>;
      const canceledId = custom.detail;
      setPendingExpenses(prev => prev.filter(p => p.id !== canceledId));
      setSelectedPendingIds(prev => prev.filter(x => x !== canceledId));
    };

    window.addEventListener('expense:canceled', onCanceled as EventListener);
    return () => window.removeEventListener('expense:canceled', onCanceled as EventListener);
  }, []);
  const [selectedExpense, setSelectedExpense] = useState<Expense | null>(null);
  const [actionType, setActionType] = useState<'approve' | 'reject' | null>(null);
  const [rejectionReason, setRejectionReason] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [historyFilter, setHistoryFilter] = useState<string>('approved'); // Changed default to 'approved'
  const [selectedPendingIds, setSelectedPendingIds] = useState<string[]>([]);
  const [selectedHistoryIds, setSelectedHistoryIds] = useState<string[]>([]);
  const [selectedReceiptUrl, setSelectedReceiptUrl] = useState<string | null>(null);
  const [selectedReceiptName, setSelectedReceiptName] = useState<string | null>(null);

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

  const handleViewReceipt = (expenseId: string) => {
    const all = [...pendingExpenses, ...approvedExpenses, ...rejectedExpenses];
    const expense = all.find(e => e.id === expenseId);
    if (!expense || !expense.receipt) {
      alert('Comprovante não encontrado.');
      return;
    }
    setSelectedReceiptUrl(expense.receipt);
    setSelectedReceiptName(`comprovante-${expense.id}`);
  };

  const handleToggleSelectPending = (id: string) => {
    setSelectedPendingIds(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    );
  };

  const handleToggleSelectHistory = (id: string) => {
    setSelectedHistoryIds(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    );
  };

  const handleDownloadMultipleReceipts = (ids: string[]) => {
    console.log('Downloading receipts for:', ids);
    alert(`Baixando ${ids.length} comprovante(s)...`);
  };

  // Filter functions
  const filterExpenses = (expenses: Expense[]) => {
    return expenses.filter(expense =>
      expense.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.amount.toString().includes(searchQuery)
    );
  };

  const filteredPending = filterExpenses(pendingExpenses);

  const filteredHistory = [...approvedExpenses, ...rejectedExpenses].filter(expense => {
    const matchesSearch =
      expense.employeeName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      expense.amount.toString().includes(searchQuery);

    const matchesFilter =
      historyFilter === 'all' ||
      (historyFilter === 'approved' && (expense.status === 'managerApproved' || expense.status === 'financeApproved')) ||
      (historyFilter === 'rejected' && expense.status === 'rejected');

    return matchesSearch && matchesFilter;
  });

  const renderExpenseTable = (
    expenses: Expense[],
    showActions: boolean = false,
    selectedIds: string[] = [],
    onToggleSelect?: (id: string) => void
  ) => {
    return (
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              {onToggleSelect && (
                <th className="text-left py-3 px-4 w-12"></th>
              )}
              <th className="text-left py-3 px-4 text-gray-700">{t.approval.employee}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.date}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.category}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.expenses.description}</th>
              <th className="text-right py-3 px-4 text-gray-700">{t.expenses.amount}</th>
              {!showActions && (
                <th className="text-center py-3 px-4 text-gray-700">{t.expenses.receipt}</th>
              )}
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
                  {onToggleSelect && (
                    <td className="py-3 px-4">
                      <Checkbox
                        checked={selectedIds.includes(expense.id)}
                        onCheckedChange={() => onToggleSelect(expense.id)}
                      />
                    </td>
                  )}
                  <td className="py-3 px-4 text-gray-900">
                    {expense.employeeName}
                  </td>
                  <td className="py-3 px-4 text-gray-700">
                    {formatDate(expense.date, language)}
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
                    R$ {expense.amount.toFixed(2)}
                  </td>
                  {!showActions && (
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center">
                        <Button
                          size="sm"
                          variant="outline"
                          className="border-gray-300 text-gray-700 hover:bg-gray-50"
                          onClick={() => handleViewReceipt(expense.id)}
                        >
                          <FileText className="w-4 h-4 mr-1" />
                          {t.approval.viewReceipt}
                        </Button>
                      </div>
                    </td>
                  )}
                  {showActions && (
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          className="border-gray-300 text-gray-700 hover:bg-gray-50"
                          onClick={() => handleViewReceipt(expense.id)}
                        >
                          <FileText className="w-4 h-4 mr-1" />
                          {t.approval.viewReceipt}
                        </Button>
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
                  {filteredPending.length} {t.approval.pending}
                </CardDescription>
                {/* Search Bar */}
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
                  {selectedPendingIds.length > 0 && (
                    <Button
                      variant="outline"
                      onClick={() => handleDownloadMultipleReceipts(selectedPendingIds)}
                    >
                      <Download className="w-4 h-4 mr-2" />
                      {t.expenses.downloadReceipt} {selectedPendingIds.length} {t.approval.receipts}
                    </Button>
                  )}
                </div>
              </CardHeader>
              <CardContent>
                {renderExpenseTable(
                  filteredPending,
                  true,
                  selectedPendingIds,
                  handleToggleSelectPending
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="history" className="space-y-6">
            <Card>
              <CardHeader>
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="flex items-center gap-2">
                      {historyFilter === 'approved' ? (
                        <CheckCircle className="w-5 h-5 text-green-600" />
                      ) : (
                        <XCircle className="w-5 h-5 text-red-600" />
                      )}
                      {historyFilter === 'approved' ? t.approval.approved : t.approval.rejected}
                    </CardTitle>
                    <CardDescription>
                      {historyFilter === 'approved' ? approvedExpenses.length : rejectedExpenses.length} {t.approval.checkedExpenses}
                    </CardDescription>
                  </div>

                  <div className="flex items-center gap-2">
                    <Tabs value={historyFilter} onValueChange={(v) => setHistoryFilter(v)}>
                      <TabsList>
                        <TabsTrigger value="approved" className="gap-2">
                          {t.approval.approved}
                        </TabsTrigger>
                        <TabsTrigger value="rejected">
                          {t.approval.rejected}
                        </TabsTrigger>
                      </TabsList>
                    </Tabs>
                  </div>
                </div>

                {/* Search and actions for history */}
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

                  {selectedHistoryIds.length > 0 && (
                    <Button
                      variant="outline"
                      onClick={() => handleDownloadMultipleReceipts(selectedHistoryIds)}
                    >
                      <Download className="w-4 h-4 mr-2" />
                      {t.expenses.downloadReceipt} {selectedHistoryIds.length} {t.approval.receipts}
                    </Button>
                  )}
                </div>
              </CardHeader>

              <CardContent>
                {renderExpenseTable(filteredHistory, false, selectedHistoryIds, handleToggleSelectHistory)}
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
                  <p><strong>{t.expenses.amount}:</strong> R$ {selectedExpense.amount.toFixed(2)}</p>
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
              className={`flex-1 ${actionType === 'approve'
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

      {/* Receipt Viewer Dialog */}
      <Dialog open={!!selectedReceiptUrl} onOpenChange={(open) => !open && setSelectedReceiptUrl(null)}>
        <DialogContent className="max-w-3xl">
          <div className="flex gap-4">
            <div className="flex-1">
              {selectedReceiptUrl?.toLowerCase().endsWith('.pdf') ? (
                <object data={selectedReceiptUrl} type="application/pdf" width="100%" height="600">
                  <p>PDF não suportado. <a href={selectedReceiptUrl} target="_blank" rel="noreferrer">Abrir em nova aba</a></p>
                </object>
              ) : (
                <img src={selectedReceiptUrl!} alt={selectedReceiptName ?? 'Comprovante'} className="max-h-[70vh] w-full object-contain" />
              )}
            </div>
            <div className="flex flex-col items-start gap-2">
              <a href={selectedReceiptUrl ?? '#'} download className="w-full">
                <Button className="w-full" variant="outline">
                  <Download className="w-4 h-4 mr-2" />
                  {t.expenses.downloadReceipt}
                </Button>
              </a>
              <Button variant="outline" onClick={() => setSelectedReceiptUrl(null)}>{t.common.close}</Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
