import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestListPage } from './request-list-page';

describe('RequestListPage', () => {
  let component: RequestListPage;
  let fixture: ComponentFixture<RequestListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
