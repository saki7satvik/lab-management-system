import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
// Import your shared feature card! (Adjust path if needed)
import { FeatureCardComponent } from '../../../../shared/components/feature-card/feature-card'; 

@Component({
  selector: 'app-returns-home-page',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, FeatureCardComponent],
  templateUrl: './returns-home-page.html',
  styleUrl: './returns-home-page.scss'
})
export class ReturnsHomePage implements OnInit {
  private tokenService = inject(TokenService);
  userRole: string = '';

  ngOnInit(): void {
    this.userRole = this.tokenService.getRole() ?? 'STUDENT';
  }
}