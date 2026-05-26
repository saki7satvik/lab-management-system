// page-header.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-header-wrapper">
      <div class="title-section">
        @if (backRoute) {
          <button [routerLink]="backRoute" class="btn-back">⬅</button>
        }
        <h1>{{ title }}</h1>
      </div>
      <div class="action-section">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styles: [`
    .page-header-wrapper {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 1.5rem;
      width: 100%;
    }
    .title-section {
      display: flex;
      align-items: center;
      gap: 1rem;
      h1 { font-size: 2rem; font-weight: 700; color: var(--text-primary); }
    }
    .btn-back {
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid var(--glass-border);
      color: var(--text-primary);
      width: 40px; height: 40px; border-radius: 10px;
      cursor: pointer; display: flex; align-items: center; justify-content: center;
      transition: all 0.2s ease;
      &:hover { background: rgba(255, 255, 255, 0.12); border-color: var(--primary-cyan); }
    }
  `]
})
export class PageHeaderComponent {
  @Input() title: string = '';
  @Input() backRoute?: string;
}