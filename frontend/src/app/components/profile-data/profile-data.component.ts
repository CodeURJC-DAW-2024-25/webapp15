import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { UserDTO } from '../../dtos/user.dto';
import Cookies from 'js-cookie';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-profile-data',
  templateUrl: './profile-data.component.html',
  styleUrls: ['./profile-data.component.css']
})

export class ProfileDataComponent implements OnInit {
  profileForm: FormGroup;
  userId: number | null = null;
  profileImage: SafeUrl | string = 'assets/images/default-profile.png';
  isAdmin: boolean = false;
  activeSection: string = 'profile-info';
  selectedFile: File | null = null;
  isLoading: boolean = false;

  private readonly API_URL = "/api/v1";

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private sanitizer: DomSanitizer,
    private loginService: LoginService
  ) {
    this.profileForm = this.fb.group({
      firstname: ['', [Validators.required]],
      lastname: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.userId = +params['id'];
      if (this.userId) {
        this.loadUserProfile();
      } else {
        // Fallback a cookies si no hay ID en ruta
        this.loadUserDataFromCookies();
      }
    });
  }

  loadUserDataFromCookies(): void {
    const userId = Cookies.get('userId');
    const username = Cookies.get('username');
    const email = Cookies.get('email');
    const firstname = Cookies.get('firstname');
    const lastname = Cookies.get('lastname');

    this.userId = userId ? parseInt(userId, 10) : null;

    this.profileForm.patchValue({
      username: username || '',
      email: email || '',
      firstname: firstname || '',
      lastname: lastname || ''
    });
  }

  loadUserProfile(): void {
    if (!this.userId) return;
    this.isLoading = true;
    
    this.http.get<UserDTO>(`${this.API_URL}/users/${this.userId}`, {
      withCredentials: true
    }).subscribe({
      next: (user: UserDTO) => {
        // Actualiza el formulario con los datos del usuario
        this.profileForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastName, // Asegúrate que coincide con la estructura
          username: user.username,
          email: user.email
        });
  
        if (user.imageString) {
          this.profileImage = this.sanitizer.bypassSecurityTrustUrl(
            `data:image/png;base64,${user.imageString}`
          );
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error al cargar el perfil:', err);
        this.isLoading = false;
        // Opcional: cargar datos de cookies como fallback
        this.loadUserDataFromCookies();
      }
    });
  }

  loadProfileImage(): void {
    if (!this.userId) return;

    this.http.get(`${this.API_URL}/users/${this.userId}/image`, {
      responseType: 'blob',
      withCredentials: true
    }).subscribe({
      next: (blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.profileImage = this.sanitizer.bypassSecurityTrustUrl(objectUrl);
      },
      error: () => {
        this.profileImage = 'assets/images/default-profile.png';
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.profileImage = this.sanitizer.bypassSecurityTrustUrl(e.target.result);
      };
      reader.readAsDataURL(file);
    }
  }

  updateProfile(): void {
    if (this.profileForm.invalid || !this.userId) return;
    this.isLoading = true;

    const updatedUser: UserDTO = {
      id: this.userId,
      firstname: this.profileForm.value.firstname,
      lastName: this.profileForm.value.lastname,
      username: this.profileForm.value.username,
      email: this.profileForm.value.email,
      password: 'pass', //Eliminar atributos que no se requieran actualizar
      roles: ['USER'],
      orders: null, //Eliminar atributos que no se requieran actualizar
      imageString: null //Eliminar atributos que no se requieran actualizar
    };

    this.http.put<UserDTO>(`${this.API_URL}/users/${this.userId}`, updatedUser, {
      withCredentials: true
    }).subscribe({
      next: (user: UserDTO) => {
        this.profileForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastName,
          username: user.username,
          email: user.email
        });

        Cookies.set('firstname', user.firstname);
        Cookies.set('lastname', user.lastName);
        Cookies.set('username', user.username);
        Cookies.set('email', user.email);


        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error updating profile:', err);
        this.isLoading = false;
      }
    });
  }

  changePhoto(): void {
    document.getElementById('photoInput')?.click();
  }

  logout(): void {
    this.http.post(`${this.API_URL}/auth/logout`, {}, {
      withCredentials: true
    }).subscribe({
      complete: () => {
        localStorage.clear();
        sessionStorage.clear();
        Cookies.remove('userId');
        Cookies.remove('username');
        Cookies.remove('email');
        Cookies.remove('firstname');
        Cookies.remove('lastname');
        Cookies.remove('isAdmin');
        Cookies.remove('AccessToken');
        Cookies.remove('AuthToken');
        Cookies.remove('RefreshToken');
        window.location.href = '/';
      },
      error: (err) => {
        console.error('Error durante logout:', err);
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = '/';
      }
    });
  }

  navigateTo(section: string): void {
    this.activeSection = section;
  }
}
