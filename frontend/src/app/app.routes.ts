import { Routes } from '@angular/router';

export const routes: Routes = [
	{ path: '', redirectTo: 'login', pathMatch: 'full' },
	{ path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
	{ path: 'main', loadComponent: () => import('./pages/main/main.component').then(m => m.MainComponent) },
	{ path: '**', redirectTo: 'login' }
];



