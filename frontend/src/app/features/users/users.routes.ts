import { Routes } from '@angular/router';

import { UserListPage }
from './pages/user-list-page/user-list-page';
import { CreateUserPage } from './pages/create-user-page/create-user-page';
import { UsersHomePage } from './pages/users-home-page/users-home-page';

export const USERS_ROUTES: Routes = [

  {
    path: '',
    component: UsersHomePage
  },
  {
    path: 'list',
    component: UserListPage
  },
  {
    path: 'create',
    component: CreateUserPage
  }
];