import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentFilters } from './component-filters';

describe('ComponentFilters', () => {
  let component: ComponentFilters;
  let fixture: ComponentFixture<ComponentFilters>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentFilters],
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentFilters);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
