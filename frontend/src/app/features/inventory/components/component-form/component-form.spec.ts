import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComponentForm } from './component-form';

describe('ComponentForm', () => {
  let component: ComponentForm;
  let fixture: ComponentFixture<ComponentForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
