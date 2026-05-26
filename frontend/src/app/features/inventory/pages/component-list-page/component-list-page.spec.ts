import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentListPage } from './component-list-page';

describe('ComponentListPage', () => {
  let component: ComponentListPage;
  let fixture: ComponentFixture<ComponentListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
