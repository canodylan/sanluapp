import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';
import { DashboardLayoutComponent } from './components/dashboard-layout/dashboard-layout.component';

export const routes: Routes = [
	{ path: '', redirectTo: 'main', pathMatch: 'full' },
	{ path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
	{
		path: '',
		component: DashboardLayoutComponent,
		canActivate: [authGuard],
		children: [
			{ path: 'home', redirectTo: 'main', pathMatch: 'full' },
			{ path: 'main', loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent) },
			{ path: 'quota', loadComponent: () => import('./pages/quota/quota.component').then(m => m.QuotaComponent) },
			{ path: 'expenses/new', loadComponent: () => import('./pages/expense-request/expense-request.component').then(m => m.ExpenseRequestComponent) },
			{ path: 'settings', loadComponent: () => import('./pages/settings/settings.component').then(m => m.SettingsComponent) },
			{
				path: 'admin',
				canActivate: [adminGuard],
				children: [
					{ path: '', loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent) },
					{ path: 'members', loadComponent: () => import('./pages/admin-members/admin-members.component').then(m => m.AdminMembersComponent), canActivate: [adminGuard] },
					{ path: 'fees', loadComponent: () => import('./pages/admin-fees/admin-fees.component').then(m => m.AdminFeesComponent), canActivate: [adminGuard] },
					{ path: 'finance', loadComponent: () => import('./pages/admin-finance/admin-finance.component').then(m => m.AdminFinanceComponent), canActivate: [adminGuard] },
					{ path: 'accounts', loadComponent: () => import('./pages/admin-accounts/admin-accounts.component').then(m => m.AdminAccountsComponent), canActivate: [adminGuard] }
				]
			},
			{ path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent), canActivate: [adminGuard] }
		]
	},
	{ path: '**', redirectTo: 'login' }
];



