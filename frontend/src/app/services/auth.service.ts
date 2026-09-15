import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { Role, TokenResponse, User } from '../models';

const TOKEN_KEY = 'sp.token';
const USER_KEY = 'sp.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly userSignal = signal<User | null>(this.readUser());
  readonly user = this.userSignal.asReadonly();
  readonly loggedIn = computed(() => !!this.userSignal());

  constructor(private http: HttpClient, private router: Router) {}

  token(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  hasRole(...roles: Role[]): boolean {
    const current = this.userSignal();
    return !!current?.roles?.some(r => roles.includes(r));
  }

  isStaff(): boolean {
    return this.hasRole('ADMIN', 'STAFF');
  }

  login(email: string, password: string) {
    return this.http.post<TokenResponse>('/api/auth/login', { email, password }).pipe(
      tap(res => this.store(res))
    );
  }

  register(body: { email: string; password: string; fullName: string; phone?: string }) {
    return this.http.post<TokenResponse>('/api/auth/register', body).pipe(
      tap(res => this.store(res))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSignal.set(null);
    void this.router.navigateByUrl('/login');
  }

  private store(res: TokenResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    localStorage.setItem(USER_KEY, JSON.stringify(res.user));
    this.userSignal.set(res.user);
  }

  private readUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }
}
