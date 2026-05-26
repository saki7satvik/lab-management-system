import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessReturnPage } from './process-return-page';

describe('ProcessReturnPage', () => {
  let component: ProcessReturnPage;
  let fixture: ComponentFixture<ProcessReturnPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcessReturnPage],
    }).compileComponents();

    fixture = TestBed.createComponent(ProcessReturnPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
