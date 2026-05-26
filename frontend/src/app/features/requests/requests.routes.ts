import { Route } from '@angular/router';
import { RequestListPage } from './pages/request-list-page/request-list-page';
import { CreateRequestPage } from './pages/create-request-page/create-request-page';
import { RequestDetailsPage } from './pages/request-details-page/request-details-page';

export const REQUESTS_ROUTES: Route[] = [
  { path: '', component: RequestListPage },
  { path: 'create', component: CreateRequestPage },
  { path: ':id', component: RequestDetailsPage }, // Must be last so 'create' doesn't get treated as an ID
];