import { useState } from 'react';
import { AlertTriangle, CheckCircle, AlertCircle } from 'lucide-react';
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

interface AlertsPageProps {
  t: TranslationKeys;
}

interface Alert {
  id: string;
  type: 'limit_exceeded' | 'pending_approval' | 'policy_violation' | 'duplicate';
  message: string;
  employeeName: string;
  amount?: number;
  triggeredAt: string;
  status: 'active' | 'resolved';
  resolvedBy?: string;
  resolvedAt?: string;
  resolveNote?: string;
}

// Mock data - switch for real data integration
const mockActiveAlerts: Alert[] = [
  {
    id: '1',
    type: 'limit_exceeded',
    message: 'Category limit "Meal" exceeded in USD 150',
    employeeName: 'João Santos',
    amount: 1200,
    triggeredAt: '2025-12-24T10:30:00',
    status: 'active',
  },
  {
    id: '2',
    type: 'policy_violation',
    message: 'Expense without attached proof',
    employeeName: 'Mariana Souza',
    amount: 450,
    triggeredAt: '2025-12-24T14:15:00',
    status: 'active',
  },
  {
    id: '3',
    type: 'pending_approval',
    message: 'Approval pending for more than 5 days',
    employeeName: 'Pedro Lima',
    amount: 3200,
    triggeredAt: '2025-12-23T09:00:00',
    status: 'active',
  },
  {
    id: '4',
    type: 'duplicate',
    message: 'Possible duplicate expense detected',
    employeeName: 'Ana Costa',
    amount: 85.50,
    triggeredAt: '2025-12-25T11:20:00',
    status: 'active',
  },
];

const mockResolvedAlerts: Alert[] = [
  {
    id: '5',
    type: 'limit_exceeded',
    message: 'Category limit "Transport" exceeded',
    employeeName: 'Carlos Silva',
    amount: 600,
    triggeredAt: '2025-12-20T08:30:00',
    status: 'resolved',
    resolvedBy: 'Julia Oliveira',
    resolvedAt: '2025-12-21T10:00:00',
    resolveNote: 'Approved on an exceptional basis due to urgent travel for a client.',
  },
  {
    id: '6',
    type: 'policy_violation',
    message: 'Insufficient expense description',
    employeeName: 'João Santos',
    amount: 120,
    triggeredAt: '2025-12-19T15:45:00',
    status: 'resolved',
    resolvedBy: 'Ana Costa',
    resolvedAt: '2025-12-20T09:30:00',
    resolveNote: 'Employee provided detailed description. Approved.',
  },
];

export function AlertsPage({ t }: AlertsPageProps) {
  const { user } = useAuth();
  const [activeAlerts] = useState<Alert[]>(mockActiveAlerts);
  const [resolvedAlerts] = useState<Alert[]>(mockResolvedAlerts);
  const [selectedAlert, setSelectedAlert] = useState<Alert | null>(null);
  const [resolveNote, setResolveNote] = useState('');

  if (!user) return null;

  const handleResolve = (alert: Alert) => {
    setSelectedAlert(alert);
    setResolveNote('');
  };

  const handleConfirmResolve = () => {
    // Handle alert resolution
    setSelectedAlert(null);
    setResolveNote('');
  };

  const handleCancelResolve = () => {
    setSelectedAlert(null);
    setResolveNote('');
  };

  const getAlertTypeColor = (type: Alert['type']) => {
    switch (type) {
      case 'limit_exceeded':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'pending_approval':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'policy_violation':
        return 'bg-orange-100 text-orange-800 border-orange-200';
      case 'duplicate':
        return 'bg-purple-100 text-purple-800 border-purple-200';
    }
  };

  const getAlertIcon = (type: Alert['type']) => {
    switch (type) {
      case 'limit_exceeded':
      case 'policy_violation':
        return <AlertTriangle className="w-4 h-4" />;
      case 'pending_approval':
        return <AlertCircle className="w-4 h-4" />;
      case 'duplicate':
        return <AlertTriangle className="w-4 h-4" />;
    }
  };

  const renderAlertsTable = (alerts: Alert[], showActions: boolean = false) => {
    return (
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th className="text-left py-3 px-4 text-gray-700">{t.alerts.type}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.approval.employee}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.alerts.message}</th>
              <th className="text-right py-3 px-4 text-gray-700">{t.expenses.amount}</th>
              <th className="text-left py-3 px-4 text-gray-700">{t.alerts.triggered}</th>
              {!showActions && (
                <>
                  <th className="text-left py-3 px-4 text-gray-700">{t.approval.reviewedBy}</th>
                  <th className="text-left py-3 px-4 text-gray-700">{t.alerts.resolveNote}</th>
                </>
              )}
              {showActions && (
                <th className="text-center py-3 px-4 text-gray-700">{t.expenses.actions}</th>
              )}
            </tr>
          </thead>
          <tbody>
            {alerts.map((alert) => {
              const typeKey = alert.type as keyof typeof t.alerts.types;
              
              return (
                <tr key={alert.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4">
                    <Badge className={getAlertTypeColor(alert.type)}>
                      <span className="flex items-center gap-1">
                        {getAlertIcon(alert.type)}
                        {t.alerts.types[typeKey]}
                      </span>
                    </Badge>
                  </td>
                  <td className="py-3 px-4 text-gray-900">
                    {alert.employeeName}
                  </td>
                  <td className="py-3 px-4 text-gray-700 max-w-xs">
                    {alert.message}
                  </td>
                  <td className="py-3 px-4 text-right text-gray-900">
                    {alert.amount ? `USD ${alert.amount.toFixed(2)}` : '-'}
                  </td>
                  <td className="py-3 px-4 text-gray-700 text-sm">
                    {new Date(alert.triggeredAt).toLocaleString('pt-BR')}
                  </td>
                  {!showActions && (
                    <>
                      <td className="py-3 px-4 text-gray-700">
                        {alert.resolvedBy || '-'}
                      </td>
                      <td className="py-3 px-4 text-gray-700 max-w-xs truncate">
                        {alert.resolveNote || '-'}
                      </td>
                    </>
                  )}
                  {showActions && (
                    <td className="py-3 px-4">
                      <div className="flex items-center justify-center">
                        <Button
                          size="sm"
                          className="bg-[#E60000] hover:bg-[#CC0000] text-white"
                          onClick={() => handleResolve(alert)}
                        >
                          <CheckCircle className="w-4 h-4 mr-1" />
                          {t.alerts.resolve}
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
            <h1 className="text-gray-900 mb-2">{t.alerts.title}</h1>
            <p className="text-gray-600">{t.alerts.subtitle}</p>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto px-6 py-8">
        <Tabs defaultValue="active" className="space-y-6">
          <TabsList>
            <TabsTrigger value="active" className="gap-2">
              <AlertTriangle className="w-4 h-4" />
              {t.alerts.active}
              <Badge className="ml-2 bg-red-100 text-red-800 border-red-200">
                {activeAlerts.length}
              </Badge>
            </TabsTrigger>
            <TabsTrigger value="resolved" className="gap-2">
              <CheckCircle className="w-4 h-4" />
              {t.alerts.resolved}
            </TabsTrigger>
          </TabsList>

          <TabsContent value="active">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <AlertTriangle className="w-5 h-5 text-[#E60000]" />
                  {t.alerts.active}
                </CardTitle>
                <CardDescription>
                  {activeAlerts.length} {t.alerts.activeAttention}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {renderAlertsTable(activeAlerts, true)}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="resolved">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <CheckCircle className="w-5 h-5 text-green-600" />
                  {t.alerts.resolved}
                </CardTitle>
                <CardDescription>
                  {resolvedAlerts.length} {t.alerts.alertResolved}
                </CardDescription>
              </CardHeader>
              <CardContent>
                {renderAlertsTable(resolvedAlerts)}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      {/* Resolve Dialog */}
      <Dialog open={!!selectedAlert} onOpenChange={(open) => !open && handleCancelResolve()}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{t.alerts.resolve}</DialogTitle>
            <DialogDescription>
              {selectedAlert && (
                <div className="mt-4 space-y-2 text-sm">
                  <p><strong>{t.alerts.type}:</strong> {t.alerts.types[selectedAlert.type as keyof typeof t.alerts.types]}</p>
                  <p><strong>{t.approval.employee}:</strong> {selectedAlert.employeeName}</p>
                  <p><strong>{t.alerts.message}:</strong> {selectedAlert.message}</p>
                  {selectedAlert.amount && (
                    <p><strong>{t.expenses.amount}:</strong> USD {selectedAlert.amount.toFixed(2)}</p>
                  )}
                </div>
              )}
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-2">
            <Label htmlFor="resolveNote">{t.alerts.resolveNote}</Label>
            <Textarea
              id="resolveNote"
              placeholder={t.alerts.resolveNotePlaceholder}
              value={resolveNote}
              onChange={(e) => setResolveNote(e.target.value)}
              rows={4}
              required
            />
          </div>

          <div className="flex gap-2">
            <Button
              variant="outline"
              className="flex-1"
              onClick={handleCancelResolve}
            >
              {t.expenses.cancel}
            </Button>
            <Button
              className="flex-1 bg-[#E60000] hover:bg-[#CC0000] text-white"
              onClick={handleConfirmResolve}
              disabled={!resolveNote.trim()}
            >
              {t.alerts.resolve}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
