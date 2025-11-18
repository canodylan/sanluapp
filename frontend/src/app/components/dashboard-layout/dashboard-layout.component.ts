import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';
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

  user = this.authService.getCurrentUser();
  isDarkTheme = false;
  isLoggingOut = false;
  navLinks = this.buildNavLinks();
  readonly trackByLink = (_: number, link: NavLink) => link.route;
  readonly isMobile$ = this.deviceService.isMobile$;
  mobileMenuOpen = false;

  constructor() {
    this.themeService.isDarkTheme$
      .pipe(takeUntilDestroyed())
      .subscribe((isDark) => (this.isDarkTheme = isDark));
  }

  private buildNavLinks(): NavLink[] {
    // Build once so RouterLinkActive does not thrash the view
    const links: NavLink[] = [{ label: 'Cuotas', route: '/quota' }];
    if (this.authService.hasRole('ADMIN')) {
      links.push({ label: 'Admin', route: '/admin' });
    }
    return links;
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

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  handleNavSelection(): void {
    if (this.deviceService.currentDeviceType === 'mobile') {
      this.mobileMenuOpen = false;
    }
  }
}
