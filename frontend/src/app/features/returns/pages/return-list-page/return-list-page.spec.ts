import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturnListPage } from './return-list-page';

describe('ReturnListPage', () => {
  let component: ReturnListPage;
  let fixture: ComponentFixture<ReturnListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReturnListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ReturnListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
