import { Routes } from '@angular/router';
import { AuthLayout } from './layouts/auth-layout/auth-layout';
import { DashboardLayout } from './layouts/dashboard-layout/dashboard-layout';
import { authGuard } from './core/guards/auth.guard';
import { LoginPage } from './features/auth/pages/login-page/login-page';

export const routes: Routes = [
  
    {
    path: '',
    component: AuthLayout, // This serves as the structural shell
    children: [
      {
        path: 'login',
        component: LoginPage // This will now inject directly into the <router-outlet>
      },
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      }
    ]
  },
    {
        path: 'dashboard',
        component: DashboardLayout,
        canActivate: [authGuard],
        children: [
            {
                path: 'users',
                loadChildren: () =>
                    import(
                        './features/users/users.routes'
                    ).then(
                        m => m.USERS_ROUTES
                    )
            },
            {
                path: '',

                redirectTo: 'login',

                pathMatch: 'full'
            },
            {
                path: 'inventory',
                loadChildren: () =>
                    import(
                        './features/inventory/inventory.routes'
                    ).then(
                        m => m.INVENTORY_ROUTES
                    )
            },
            {
                path: 'requests',
                loadChildren: () => 
                    import(
                        './features/requests/requests.routes'
                    ).then(
                        m => m.REQUESTS_ROUTES
                    )
            },
            {
                path: 'returns',
                loadChildren: () =>
                    import(
                        './features/returns/returns.routes'
                    ).then(
                        m => m.RETURNS_ROUTES
                    )
            }
        ]
    }
];
