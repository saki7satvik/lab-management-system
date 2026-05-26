import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockUpdateDialog } from './stock-update-dialog';

describe('StockUpdateDialog', () => {
  let component: StockUpdateDialog;
  let fixture: ComponentFixture<StockUpdateDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockUpdateDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(StockUpdateDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
