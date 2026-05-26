import { Routes } from '@angular/router';
import { ReturnsHomePage } from './pages/returns-home-page/returns-home-page';
import { ActiveCheckoutsPage } from './pages/active-checkouts-page/active-checkouts-page';
import { ReturnListPage } from './pages/return-list-page/return-list-page';
import { ProcessReturnPage } from './pages/process-return-page/process-return-page';
import { ReturnsDetailsPage } from './pages/returns-details-page/returns-details-page';

export const RETURNS_ROUTES: Routes = [
  { path: '', component: ReturnsHomePage }, // The 2-Card Dashboard
  { path: 'active', component: ActiveCheckoutsPage }, // The queue of approved requests
  { path: 'process/:requestId', component: ProcessReturnPage }, // The form we already built
  { path: 'history', component: ReturnListPage }, // The history table we already built
  { path: 'history/:returnId', component: ReturnsDetailsPage } // The receipt we already built
];