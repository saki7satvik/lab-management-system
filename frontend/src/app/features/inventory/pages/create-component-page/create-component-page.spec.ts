import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateComponentPage } from './create-component-page';

describe('CreateComponentPage', () => {
  let component: CreateComponentPage;
  let fixture: ComponentFixture<CreateComponentPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateComponentPage],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateComponentPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
