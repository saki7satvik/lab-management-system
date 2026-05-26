import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryHomePage } from './inventory-home-page';

describe('InventoryHomePage', () => {
  let component: InventoryHomePage;
  let fixture: ComponentFixture<InventoryHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryHomePage],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
