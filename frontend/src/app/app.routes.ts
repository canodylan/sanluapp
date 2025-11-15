import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
	{ path: '', redirectTo: 'login', pathMatch: 'full' },
	{ path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
	{ path: 'home', redirectTo: 'main', pathMatch: 'full' },
	{ path: 'main', loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent), canActivate: [authGuard] },
	{ path: '**', redirectTo: 'login' }
];



