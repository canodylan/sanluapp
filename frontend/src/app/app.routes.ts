import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
	{ path: '', redirectTo: 'login', pathMatch: 'full' },
	{ path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
	{ path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent), canActivate: [adminGuard] },
	{ path: 'home', redirectTo: 'main', pathMatch: 'full' },
	{ path: 'main', loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent), canActivate: [authGuard] },
	{ path: 'quota', loadComponent: () => import('./pages/quota/quota.component').then(m => m.QuotaComponent), canActivate: [authGuard] },
	{ path: 'admin', loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent), canActivate: [adminGuard] },
	{ path: '**', redirectTo: 'login' }
];



