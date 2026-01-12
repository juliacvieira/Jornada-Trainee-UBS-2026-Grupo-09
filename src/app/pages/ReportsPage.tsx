import { useState } from 'react';
import { BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { TrendingUp, DollarSign, Clock, AlertTriangle, Download } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Button } from '../components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';
import type { TranslationKeys, Language } from '../translations';
import { useAuth } from '../hooks/useAuth';

interface ReportsPageProps {
  t: TranslationKeys;
  language: Language;
}

// Mock data for charts - switch for real data integration
const expensesByCategory = [
  { name: 'Travel', value: 12500, color: '#E60000' },
  { name: 'Meal', value: 4250, color: '#FF6B6B' },
  { name: 'Transport', value: 2100, color: '#FFA07A' },
  { name: 'Others', value: 1850, color: '#FFB347' },
];

// Budget data with spent vs remaining percentages
const budgetLimit = 150000; // Total budget limit
const budgetData = [
  { monthIndex: 1, spent: 12000 },
  { monthIndex: 2, spent: 14000 },
  { monthIndex: 3, spent: 11000 },
  { monthIndex: 4, spent: 16000 },
  { monthIndex: 5, spent: 18000 },
  { monthIndex: 6, spent: 17500 },
  { monthIndex: 7, spent: 15000 },
  { monthIndex: 8, spent: 18500 },
  { monthIndex: 9, spent: 16200 },
  { monthIndex: 10, spent: 19800 },
  { monthIndex: 11, spent: 21500 },
  { monthIndex: 12, spent: 20700 },
].map(d => ({
  ...d,
  remaining: budgetLimit - d.spent,
  spentPercent: (d.spent / budgetLimit) * 100,
  remainingPercent: ((budgetLimit - d.spent) / budgetLimit) * 100,
}));

const departmentData: { department: string; expenses: number; employees: number }[] = [
  { department: 'IT', expenses: 28500, employees: 45 },
  { department: 'Sales', expenses: 35200, employees: 62 },
  { department: 'Marketing', expenses: 22100, employees: 28 },
  { department: 'HR', expenses: 12400, employees: 18 },
  { department: 'Financial', expenses: 18900, employees: 25 },
];

const topSpenders = [
  { name: 'Ana Costa', amount: 8500, department: 'Sales' },
  { name: 'Carlos Silva', amount: 7200, department: 'IT' },
  { name: 'Mariana Souza', amount: 6800, department: 'Marketing' },
  { name: 'Pedro Santos', amount: 5900, department: 'Sales' },
  { name: 'Julia Lima', amount: 5400, department: 'IT' },
  { name: 'Roberto Alves', amount: 4850, department: 'HR' },
  { name: 'Fernanda Costa', amount: 4200, department: 'Marketing' },
  { name: 'Lucas Mendes', amount: 3900, department: 'Sales' },
  { name: 'Beatriz Rocha', amount: 3650, department: 'IT' },
  { name: 'Rafael Souza', amount: 3200, department: 'Financial' },
];

interface CustomTooltipProps {
  active?: boolean;
  payload?: Array<{
    payload: {
      month: string;
      spent: number;
      remaining: number;
    };
  }>;
}

export function ReportsPage({ t, language }: ReportsPageProps) {
  const { user } = useAuth();
  const [selectedPeriod, setSelectedPeriod] = useState('month');
  const [selectedDepartment, setSelectedDepartment] = useState('all');
  const [selectedYear, setSelectedYear] = useState('2025');
  const [selectedMonth, setSelectedMonth] = useState('all');

  if (!user) return null;

  // Localize months for budget chart (all 12 months)
  const monthKeys = ['jan', 'feb', 'mar', 'apr', 'may', 'jun', 'jul', 'aug', 'sep', 'oct', 'nov', 'dec'] as const;

  const budgetDataLocalized = budgetData.map((d) => ({
    ...d,
    month: t.reports.months[monthKeys[(d.monthIndex || 1) - 1] as keyof typeof t.reports.months],
  }));

  // Map expense category names to translations
  const categoryKeyMap: Record<string, keyof typeof t.expenses.categories> = {
    Travel: 'travel',
    Meal: 'meal',
    Transport: 'transport',
    Others: 'other',
  };

  const expensesByCategoryLocalized = expensesByCategory.map((e) => ({
    ...e,
    name: t.expenses.categories[categoryKeyMap[e.name] ?? 'other'] ?? e.name,
  }));

  // Translate department labels for X axis
  const departmentKeyMap: Record<string, keyof typeof t.employees.departments> = {
    IT: 'it',
    Sales: 'sales',
    Marketing: 'marketing',
    HR: 'hr',
    Financial: 'finance',
  };

  const topSpendersLocalized = topSpenders.map(s => ({
    ...s,
    department: t.employees.departments[departmentKeyMap[s.department] ?? ''],
  }));


  const departmentDataLocalized = departmentData.map((d: { department: string; expenses: number; employees: number }) => ({
    ...d,
    department: t.employees.departments[departmentKeyMap[d.department] ?? 'it'],
  }));

  const statsCards = [
    {
      title: t.reports.totalExpenses,
      value: 'USD 117,200',
      change: '+12.5%',
      icon: DollarSign,
      color: 'text-[#E60000]',
      bgColor: 'bg-red-50',
    },
    {
      title: t.reports.pendingApprovals,
      value: '24',
      change: '-8.2%',
      icon: Clock,
      color: 'text-yellow-600',
      bgColor: 'bg-yellow-50',
    },
    {
      title: t.reports.averageExpense,
      value: 'USD 1,245',
      change: '+5.3%',
      icon: TrendingUp,
      color: 'text-green-600',
      bgColor: 'bg-green-50',
    },
    {
      title: t.reports.alertsTriggered,
      value: '7',
      change: '-15.4%',
      icon: AlertTriangle,
      color: 'text-orange-600',
      bgColor: 'bg-orange-50',
    },
  ];

  const locale = language === 'pt' ? 'pt-BR' : 'en-US';

  const CustomTooltip = ({ active, payload }: CustomTooltipProps) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="bg-white p-4 border border-gray-200 rounded shadow-lg">
          <p className="font-semibold mb-2">{data.month}</p>
          <p className="text-sm text-gray-700">
            <span className="text-[#E60000]">● {t.reports.approvedExpensesPercent.replace(' (%)', '')}:</span> USD {data.spent.toLocaleString(locale)}
          </p>
          <p className="text-sm text-gray-700">
            <span className="text-[#10B981]">● {t.reports.remainingBudgetPercent.replace(' (%)', '')}:</span> USD {data.remaining.toLocaleString(locale)}
          </p>
          <p className="text-sm text-gray-500 mt-1">
            Total: USD {budgetLimit.toLocaleString(locale)}
          </p>
        </div>
      );
    }
    return null;
  };

  const handleExportCSV = () => {
    const lines: string[] = [];

    // Selected filters
    const selectedPeriodLabel =
      selectedPeriod === 'week' ? t.reports.periodWeek :
      selectedPeriod === 'month' ? t.reports.periodMonth :
      selectedPeriod === 'quarter' ? t.reports.periodQuarter :
      selectedPeriod === 'year' ? t.reports.periodYear : selectedPeriod;

    const selectedDepartmentLabel =
      selectedDepartment === 'all' ? t.reports.allDepartments : (t.employees.departments as any)[selectedDepartment] ?? selectedDepartment;

    const selectedMonthLabel = selectedMonth === 'all' ? t.reports.allMonths : (() => {
      const idx = Number(selectedMonth) - 1;
      const key = monthKeys[idx >= 0 && idx < monthKeys.length ? idx : 0];
      return t.reports.months[key];
    })();

    lines.push(`"Filters"`);
    lines.push(`"${t.reports.filters.period}","${selectedPeriodLabel}"`);
    lines.push(`"${t.reports.filters.department}","${selectedDepartmentLabel}"`);
    lines.push(`"${t.reports.filters.year}","${selectedYear}"`);
    lines.push(`"${t.reports.filters.month}","${selectedMonthLabel}"`);
    lines.push('');

    // Expenses by Category
    lines.push(`"${t.reports.expensesByCategory}"`);
    lines.push(`"${t.expenses.category}","${t.expenses.amount}"`);
    expensesByCategoryLocalized.forEach(e => {
      lines.push(`"${e.name}","${e.value}"`);
    });
    lines.push('');

    // Budget Utilization (now includes month index)
    lines.push(`"${t.reports.budgetUtilization}"`);
    lines.push(`"${t.reports.csv.month}","${t.reports.csv.monthIndex || 'Month #'}","${t.reports.csv.spent}","${t.reports.csv.remaining}","${t.reports.csv.spentPercent}","${t.reports.csv.remainingPercent}"`);
    budgetDataLocalized.forEach(d => {
      lines.push(`"${d.month}","${d.monthIndex}","${d.spent}","${d.remaining}","${d.spentPercent.toFixed(2)}","${d.remainingPercent.toFixed(2)}"`);
    });
    lines.push('');

    // Department Overview
    lines.push(`"${t.reports.departmentOverview}"`);
    lines.push(`"${t.reports.department}","${t.reports.csv.spent}","${t.reports.csv.employees}"`);
    departmentDataLocalized.forEach(d => {
      lines.push(`"${d.department}","${d.expenses}","${d.employees}"`);
    });
    lines.push('');

    // Top Spenders
    lines.push(`"${t.reports.topSpenders}"`);
    lines.push(`"Name","${t.expenses.amount}","${t.reports.department}"`);
    topSpendersLocalized.forEach(s => {
      lines.push(`"${s.name}","${s.amount}","${s.department}"`);
    });

    const csv = lines.join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const fileName = `reports_export_${new Date().toISOString().slice(0, 10)}.csv`;
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="mx-auto px-6 py-8">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-gray-900 mb-2">{t.reports.title}</h1>
              <p className="text-gray-600">{t.reports.subtitle}</p>
            </div>
            <Button className="bg-[#E60000] hover:bg-[#CC0000] text-white" onClick={handleExportCSV}>
              <Download className="w-4 h-4 mr-2" />
              {t.reports.export}
            </Button>
          </div>

          {/* Filters */}
          <div className="flex gap-4 flex-wrap">
            <Select value={selectedPeriod} onValueChange={setSelectedPeriod}>
              <SelectTrigger className="w-[160px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="week">{t.reports.periodWeek}</SelectItem>
                <SelectItem value="month">{t.reports.periodMonth}</SelectItem>
                <SelectItem value="quarter">{t.reports.periodQuarter}</SelectItem>
                <SelectItem value="year">{t.reports.periodYear}</SelectItem>
              </SelectContent>
            </Select>

            <Select value={selectedDepartment} onValueChange={setSelectedDepartment}>
              <SelectTrigger className="w-[210px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t.reports.allDepartments}</SelectItem>
                <SelectItem value="it">{t.employees.departments.it}</SelectItem>
                <SelectItem value="sales">{t.employees.departments.sales}</SelectItem>
                <SelectItem value="marketing">{t.employees.departments.marketing}</SelectItem>
                <SelectItem value="hr">{t.employees.departments.hr}</SelectItem>
                <SelectItem value="finance">{t.employees.departments.finance}</SelectItem>
              </SelectContent>
            </Select>

            <Select value={selectedYear} onValueChange={setSelectedYear}>
              <SelectTrigger className="w-[110px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="2026">2026</SelectItem>
                <SelectItem value="2025">2025</SelectItem>
                <SelectItem value="2024">2024</SelectItem>
                <SelectItem value="2023">2023</SelectItem>
              </SelectContent>
            </Select>

            <Select value={selectedMonth} onValueChange={setSelectedMonth}>
              <SelectTrigger className="w-[160px]">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">{t.reports.allMonths}</SelectItem>
                <SelectItem value="1">{t.reports.months.jan}</SelectItem>
                <SelectItem value="2">{t.reports.months.feb}</SelectItem>
                <SelectItem value="3">{t.reports.months.mar}</SelectItem>
                <SelectItem value="4">{t.reports.months.apr}</SelectItem>
                <SelectItem value="5">{t.reports.months.may}</SelectItem>
                <SelectItem value="6">{t.reports.months.jun}</SelectItem>
                <SelectItem value="7">{t.reports.months.jul}</SelectItem>
                <SelectItem value="8">{t.reports.months.aug}</SelectItem>
                <SelectItem value="9">{t.reports.months.sep}</SelectItem>
                <SelectItem value="10">{t.reports.months.oct}</SelectItem>
                <SelectItem value="11">{t.reports.months.nov}</SelectItem>
                <SelectItem value="12">{t.reports.months.dec}</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="mx-auto px-6 py-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {statsCards.map((stat, index) => {
            const Icon = stat.icon;
            return (
              <Card key={index}>
                <CardContent className="pt-6">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-sm text-gray-600 mb-1">{stat.title}</p>
                      <p className="text-2xl text-gray-900 mb-1">{stat.value}</p>
                      <p className={`text-sm ${stat.change.startsWith('+') ? 'text-green-600' : 'text-red-600'}`}>
                        {stat.change} vs {t.common.timeComparison}
                      </p>
                    </div>
                    <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                      <Icon className={`w-6 h-6 ${stat.color}`} />
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>

        {/* Charts Row 1 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          {/* Expenses by Category */}
          <Card>
            <CardHeader>
              <CardTitle>{t.reports.expensesByCategory}</CardTitle>
              <CardDescription>{t.reports.graphCategory}</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={expensesByCategoryLocalized}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percent }) => `${name}: ${((percent ?? 0) * 100).toFixed(0)}%`}
                    outerRadius={100}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {expensesByCategoryLocalized.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Budget Utilization - Stacked Bar Chart */}
          <Card>
            <CardHeader>
              <CardTitle>{t.reports.budgetUtilization}</CardTitle>
              <CardDescription>{t.reports.graphBudget}</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={budgetDataLocalized}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" tickFormatter={(val) => String(val).slice(0,3)} />
                  <YAxis
                    label={{ value: '%', angle: 0, position: 'top' }}
                    domain={[0, 100]}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  <Bar dataKey="spentPercent" stackId="a" fill="#10B981" name={t.reports.approvedExpensesPercent} />
                  <Bar dataKey="remainingPercent" stackId="a" fill="#E60000" name={t.reports.remainingBudgetPercent} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        {/* Charts Row 2 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Department Overview */}
          <Card>
            <CardHeader>
              <CardTitle>{t.reports.departmentOverview}</CardTitle>
              <CardDescription>{t.reports.graphDepartment}</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={departmentDataLocalized}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="department" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="expenses" fill="#E60000" name={t.reports.expensesUSD} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          {/* Top 10 Spenders with scroll */}
          <Card>
            <CardHeader>
              <CardTitle>{t.reports.topSpenders}</CardTitle>
              <CardDescription>{t.reports.graphTopExpenses}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4 max-h-[300px] overflow-y-auto pr-2">
                {topSpendersLocalized.map((spender, index) => (
                  <div key={index} className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-full ${'bg-gray-400'
                        } text-white flex items-center justify-center text-sm font-semibold`}>
                        {index + 1}
                      </div>
                      <div>
                        <p className="text-gray-900">{spender.name}</p>
                        <p className="text-sm text-gray-500">{spender.department}</p>
                      </div>
                    </div>
                    <p className="text-gray-900 font-semibold">USD {spender.amount.toLocaleString(locale)}</p>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
