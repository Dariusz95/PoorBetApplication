import { LiveAnnouncer } from '@angular/cdk/a11y';
import { Injectable, inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  private readonly toastr = inject(ToastrService);
  private readonly liveAnnouncer = inject(LiveAnnouncer);

  success(message: string, title?: string): void {
    this.toastr.success(message, title);
    this.liveAnnouncer.announce(message, 'polite');
  }

  error(message: string, title?: string): void {
    this.toastr.error(message, title);
    this.liveAnnouncer.announce(message, 'assertive');
  }

  info(message: string, title?: string): void {
    this.toastr.info(message, title);
    this.liveAnnouncer.announce(message, 'polite');
  }

  warning(message: string, title?: string): void {
    this.toastr.warning(message, title);
    this.liveAnnouncer.announce(message, 'polite');
  }

  clear(): void {
    this.toastr.clear();
  }
}
