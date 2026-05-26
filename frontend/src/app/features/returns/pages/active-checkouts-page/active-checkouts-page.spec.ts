import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveCheckoutsPage } from './active-checkouts-page';

describe('ActiveCheckoutsPage', () => {
  let component: ActiveCheckoutsPage;
  let fixture: ComponentFixture<ActiveCheckoutsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveCheckoutsPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ActiveCheckoutsPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
