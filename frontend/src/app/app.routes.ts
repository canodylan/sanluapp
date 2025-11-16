import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';
import { DashboardLayoutComponent } from './components/dashboard-layout/dashboard-layout.component';

export const routes: Routes = [
	{ path: '', redirectTo: 'login', pathMatch: 'full' },
	{ path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
	{
		path: '',
		component: DashboardLayoutComponent,
		canActivate: [authGuard],
		children: [
			{ path: 'home', redirectTo: 'main', pathMatch: 'full' },
			{ path: 'main', loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent) },
			{ path: 'quota', loadComponent: () => import('./pages/quota/quota.component').then(m => m.QuotaComponent) },
			{ path: 'admin', loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent), canActivate: [adminGuard] },
			{ path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent), canActivate: [adminGuard] }
		]
	},
	{ path: '**', redirectTo: 'login' }
];



