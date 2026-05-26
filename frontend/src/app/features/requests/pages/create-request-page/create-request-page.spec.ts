import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRequestPage } from './create-request-page';

describe('CreateRequestPage', () => {
  let component: CreateRequestPage;
  let fixture: ComponentFixture<CreateRequestPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateRequestPage],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateRequestPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
