import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReturnService } from '../../services/return.service';
import { TokenService } from '../../../../core/services/token';
import { Observable } from 'rxjs';
import { ReturnResponseModel } from '../../models/return-response.model';

@Component({
  selector: 'app-returns-details-page',
  imports: [CommonModule, PageHeaderComponent, RouterLink],
  templateUrl: './returns-details-page.html',
  styleUrl: './returns-details-page.scss',
})
export class ReturnsDetailsPage implements OnInit {
  private route = inject(ActivatedRoute);
  private returnService = inject(ReturnService);
  private tokenService = inject(TokenService);

  returnDetails$!: Observable<ReturnResponseModel>;
  userRole: string = '';

  ngOnInit(): void {
    // Determine the user's role in case we want to hide/show specific administrative data
    this.userRole = this.tokenService.getRole() ?? 'STUDENT';
    
    // Grab the ID from the route parameter
    const returnId = this.route.snapshot.paramMap.get('returnId') || '';
    
    // Fetch the data reactively
    this.returnDetails$ = this.returnService.getReturnById(returnId);
  }
}
