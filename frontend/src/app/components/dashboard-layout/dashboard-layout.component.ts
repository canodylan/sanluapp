import { Component, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService, AuthenticatedUser } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DeviceService } from '../../services/device.service';

interface NavLink {
  label: string;
  route: string;
}

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, MatIconModule, MatButtonModule],
  templateUrl: './dashboard-layout.component.html',
  styleUrls: ['./dashboard-layout.component.scss']
})
export class DashboardLayoutComponent {
  private authService = inject(AuthService);
  private themeService = inject(ThemeService);
  private router = inject(Router);
  private deviceService = inject(DeviceService);

  user: AuthenticatedUser | null = this.authService.getCurrentUser();
  isDarkTheme = false;
  isLoggingOut = false;
  navLinks = this.buildNavLinks();
  readonly trackByLink = (_: number, link: NavLink) => link.route;
  readonly isMobile$ = this.deviceService.isMobile$;
  mobileMenuOpen = false;
  userMenuOpen = false;

  constructor() {
    this.themeService.isDarkTheme$
      .pipe(takeUntilDestroyed())
      .subscribe((isDark) => (this.isDarkTheme = isDark));

    this.authService.user$
      .pipe(takeUntilDestroyed())
      .subscribe((user) => (this.user = user));
  }

  private buildNavLinks(): NavLink[] {
    // Build once so RouterLinkActive does not thrash the view
    const links: NavLink[] = [
      { label: 'Cuotas', route: '/quota' },
      { label: 'Registrar gasto', route: '/expenses/new' }
    ];
    
    return links;
  }

  get hasAdminAccess(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  get userInitials(): string {
    const name = this.user?.nickname || this.user?.username || 'Usuario';
    return name
      .split(' ')
      .map((part) => part.charAt(0).toUpperCase())
      .slice(0, 2)
      .join('') || 'US';
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  logout(): void {
    if (this.isLoggingOut) return;
    this.isLoggingOut = true;
    this.authService
      .logout()
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: () => this.router.navigate(['/login'])
      });
  }

  onLogoutFromMenu(): void {
    this.userMenuOpen = false;
    this.logout();
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  toggleUserMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.userMenuOpen = !this.userMenuOpen;
  }

  navigateTo(route: string): void {
    this.userMenuOpen = false;
    this.router.navigate([route]);
  }

  handleNavSelection(): void {
    if (this.deviceService.currentDeviceType === 'mobile') {
      this.mobileMenuOpen = false;
    }
  }

  @HostListener('document:click')
  closeUserMenu(): void {
    this.userMenuOpen = false;
  }
}
