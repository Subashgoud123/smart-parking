import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let api: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    api = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads parking slots', () => {
    api.slots().subscribe(rows => expect(rows.length).toBe(1));
    const req = http.expectOne('/api/parking-slots');
    expect(req.request.method).toBe('GET');
    req.flush([{ id: 1, slotNumber: 'C01', area: 'Car Park A', floor: 'L1', vehicleType: 'CAR', status: 'VACANT' }]);
  });
});
