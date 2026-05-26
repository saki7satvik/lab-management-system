import { Component, inject } from '@angular/core';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { FeatureCardComponent } from '../../../../shared/components/feature-card/feature-card';
import { TokenService } from '../../../../core/services/token';

@Component({
  selector: 'app-users-home-page',
  imports: [PageHeaderComponent, FeatureCardComponent],
  templateUrl: './users-home-page.html',
  styleUrl: './users-home-page.scss',
})
export class UsersHomePage {
  private tokenService = inject(TokenService);
  currentUserRole = this.tokenService.getRole();
}
