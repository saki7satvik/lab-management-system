import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestDetailsPage } from './request-details-page';

describe('RequestDetailsPage', () => {
  let component: RequestDetailsPage;
  let fixture: ComponentFixture<RequestDetailsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestDetailsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestDetailsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
