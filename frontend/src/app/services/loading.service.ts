import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private pending = 0;
  readonly active = signal(false);

  start(): void {
    this.pending++;
    this.active.set(true);
  }

  stop(): void {
    this.pending = Math.max(0, this.pending - 1);
    if (this.pending === 0) {
      this.active.set(false);
    }
  }
}
