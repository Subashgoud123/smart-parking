import { Routes } from '@angular/router';
import { authGuard, roleGuard, staffGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'app/layout' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'app',
    canActivate: [authGuard],
    loadComponent: () => import('./shell/shell.component').then(m => m.ShellComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'layout' },
      {
        path: 'layout',
        loadComponent: () => import('./pages/layout/layout.component').then(m => m.LayoutComponent)
      },
      {
        path: 'dashboard',
        canActivate: [staffGuard],
        loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'vehicles',
        loadComponent: () => import('./pages/vehicles/vehicles.component').then(m => m.VehiclesComponent)
      },
      {
        path: 'bookings',
        loadComponent: () => import('./pages/bookings/bookings.component').then(m => m.BookingsComponent)
      },
      {
        path: 'bulk',
        loadComponent: () => import('./pages/bulk/bulk.component').then(m => m.BulkComponent)
      },
      {
        path: 'gate',
        canActivate: [staffGuard],
        loadComponent: () => import('./pages/gate/gate.component').then(m => m.GateComponent)
      },
      {
        path: 'slots',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./pages/slots/slots.component').then(m => m.SlotsComponent)
      }
    ]
  },
  { path: '**', redirectTo: 'app/layout' }
];
