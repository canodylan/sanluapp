import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { distinctUntilChanged, map } from 'rxjs/operators';

export type DeviceType = 'mobile' | 'tablet' | 'desktop';

@Injectable({ providedIn: 'root' })
export class DeviceService {
  private readonly breakpointObserver = inject(BreakpointObserver);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly deviceTypeSubject = new BehaviorSubject<DeviceType>('desktop');

  readonly deviceType$ = this.deviceTypeSubject.asObservable();
  readonly isMobile$ = this.deviceType$.pipe(
    map((type) => type === 'mobile'),
    distinctUntilChanged()
  );
  readonly isDesktop$ = this.deviceType$.pipe(
    map((type) => type === 'desktop'),
    distinctUntilChanged()
  );

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.observeViewportChanges();
    }
  }

  get currentDeviceType(): DeviceType {
    return this.deviceTypeSubject.value;
  }

  private observeViewportChanges(): void {
    this.breakpointObserver
      .observe([Breakpoints.Handset, Breakpoints.Tablet, Breakpoints.Web])
      .pipe(
        map((state) => {
          if (state.breakpoints[Breakpoints.Handset]) return 'mobile';
          if (state.breakpoints[Breakpoints.Tablet]) return 'tablet';
          return 'desktop';
        }),
        distinctUntilChanged()
      )
      .subscribe((deviceType) => this.deviceTypeSubject.next(deviceType));
  }
}
