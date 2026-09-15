import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  template: `
    <div class="auth-shell">
      <mat-card class="auth-card">
        <mat-card-header><mat-card-title>Sign in</mat-card-title></mat-card-header>
        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <mat-form-field appearance="outline" class="full">
              <mat-label>Email</mat-label>
              <input matInput type="email" formControlName="email" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full">
              <mat-label>Password</mat-label>
              <input matInput type="password" formControlName="password" />
            </mat-form-field>
            <button mat-flat-button color="primary" class="full" [disabled]="form.invalid || busy">Login</button>
          </form>
          <p class="hint">Demo: customer@smartparking.local / Customer@123<br/>
            admin@smartparking.local / Admin@123 · staff@smartparking.local / Staff@123</p>
          <a routerLink="/register">Create a customer account</a>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`.full { width: 100%; display: block; margin-top: 8px; } .hint { font-size: 13px; color: #475569; }`]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  busy = false;
  form = this.fb.nonNullable.group({
    email: ['customer@smartparking.local', [Validators.required, Validators.email]],
    password: ['Customer@123', Validators.required]
  });

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    this.busy = true;
    const { email, password } = this.form.getRawValue();
    this.auth.login(email, password).subscribe({
      next: () => void this.router.navigateByUrl('/app/layout'),
      error: () => { this.busy = false; },
      complete: () => { this.busy = false; }
    });
  }
}
