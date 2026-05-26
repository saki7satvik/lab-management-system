import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturnsDetailsPage } from './returns-details-page';

describe('ReturnsDetailsPage', () => {
  let component: ReturnsDetailsPage;
  let fixture: ComponentFixture<ReturnsDetailsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReturnsDetailsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ReturnsDetailsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
