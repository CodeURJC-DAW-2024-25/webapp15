import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { UserDTO } from '../../dtos/user.dto';
import { UserService } from '../../services/user.service';
import { LoginService } from '../../services/login.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile-data',
  templateUrl: './profile-data.component.html',
  styleUrls: ['./profile-data.component.css']
})
export class ProfileDataComponent implements OnInit, OnDestroy {
  profileForm: FormGroup;
  userId: number | null | undefined = null;
  profileImage: SafeUrl | string = 'https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg'; /*Erase*/
  isAdmin: boolean = false;
  activeSection: string = 'profile-info';
  selectedFile: File | null = null;
  isLoading: boolean = false;
  private imageSubscription?: Subscription;
  private currentImageUrl?: string;

  constructor(
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private userService: UserService,
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
    this.loadCurrentUserProfile();
  }

  ngOnDestroy(): void {
    this.cleanupImageResources();
  }

  private cleanupImageResources(): void {
    if (this.imageSubscription) {
      this.imageSubscription.unsubscribe();
    }
    if (this.currentImageUrl) {
      URL.revokeObjectURL(this.currentImageUrl);
    }
  }

  loadCurrentUserProfile(): void {
    this.isLoading = true;
    
    this.loginService.getCurrentUser().subscribe({
      next: (user: UserDTO | null) => {
        if (user) {
          this.userId = user.id;
          this.profileForm.patchValue({
            firstname: user.firstname,
            lastname: user.lastName,
            username: user.username,
            email: user.email
          });
          this.loadProfileImage();
          this.isAdmin = user.roles?.includes('ADMIN') || false;
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading current user:', err);
        this.isLoading = false;
      }
    });
  }

  loadProfileImage(): void {
    if (!this.userId) return;
  
    this.cleanupImageResources();
  
    this.imageSubscription = this.userService.getUserImage(this.userId).subscribe({
      next: (blob: Blob) => {
  
        if (blob.size === 0) {
          this.profileImage = 'https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg'; /*ERASE*/
          return;
        }
  
        // Verifica el tipo MIME
        if (!blob.type.startsWith('image/')) {
          console.error('Invalid blob type:', blob.type);
          this.profileImage = 'https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg'; /*ERASE*/
          return;
        }
  
        this.currentImageUrl = URL.createObjectURL(blob);
  
        // Test: Crea una imagen temporal para verificar el blob
        const img = new Image();
        img.onload = () => {
          this.profileImage = this.userService.blobToSafeUrl(blob);
        };
        img.onerror = () => {
          console.error('Failed to load image from blob');
          this.profileImage = 'https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg'; /*ERASE*/
        };
        img.src = URL.createObjectURL(blob);
      },
      error: (err) => {
        console.error('Error loading profile image:', {
          error: err,
          status: err.status,
          message: err.message
        });
        this.profileImage = 'https://static.vecteezy.com/system/resources/previews/020/765/399/non_2x/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg'; /*ERASE*/
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;

    this.selectedFile = file;

    // Mostrar vista previa
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.profileImage = this.sanitizer.bypassSecurityTrustUrl(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  updateProfile(): void {
    if (this.profileForm.invalid || !this.userId) return;
    this.isLoading = true;

    // Primero actualizamos los datos del usuario
    const updatedUser: UserDTO = {
      id: this.userId,
      firstname: this.profileForm.value.firstname,
      lastName: this.profileForm.value.lastname,
      username: this.profileForm.value.username,
      email: this.profileForm.value.email,
      password: '', // No se actualiza la contraseña aquí
      roles: ['USER'],
      orders: []
    };

    this.userService.updateUser(this.userId, updatedUser).subscribe({
      next: (user: UserDTO) => {
        this.profileForm.patchValue({
          firstname: user.firstname,
          lastname: user.lastName,
          username: user.username,
          email: user.email
        });

        // Si hay una nueva imagen seleccionada, la subimos
        if (this.selectedFile) {
          this.uploadImage();
        } else {
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error updating profile:', err);
        this.isLoading = false;
      }
    });
  }

  uploadImage(): void {
    if (!this.selectedFile || !this.userId) {
      this.isLoading = false;
      return;
    }

    this.userService.uploadUserImage(this.userId, this.selectedFile).subscribe({
      next: () => {
        // Recargar la imagen después de subirla
        this.loadProfileImage();
        this.isLoading = false;
        this.selectedFile = null;
      },
      error: (err) => {
        console.error('Error uploading image:', err);
        this.isLoading = false;
      }
    });
  }

  changePhoto(): void {
    document.getElementById('photoInput')?.click();
  }

  logout(): void {
    this.loginService.logOut().subscribe();
  }

  navigateTo(section: string): void {
    this.activeSection = section;
  }
}