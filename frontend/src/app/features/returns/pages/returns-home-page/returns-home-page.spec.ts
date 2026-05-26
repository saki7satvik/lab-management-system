import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReturnsHomePage } from './returns-home-page';

describe('ReturnsHomePage', () => {
  let component: ReturnsHomePage;
  let fixture: ComponentFixture<ReturnsHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReturnsHomePage],
    }).compileComponents();

    fixture = TestBed.createComponent(ReturnsHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
