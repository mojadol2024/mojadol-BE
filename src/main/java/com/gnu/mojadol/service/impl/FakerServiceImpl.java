package com.gnu.mojadol.service.impl;

import com.github.javafaker.Faker;
import com.gnu.mojadol.entity.*;
import com.gnu.mojadol.repository.*;
import com.gnu.mojadol.service.AiService;
import com.gnu.mojadol.service.FakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.web.multipart.MultipartFile;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;


@Service
public class FakerServiceImpl implements FakerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private BreedRepository breedRepository;

    @Autowired
    private AiService AiService;

    @Autowired
    private PhotoRepository photoRepository;

    public void userFakeData() {
        try {
            Faker faker = new Faker(new Locale("ko"));
            List<User> fakeUsers = new ArrayList<>();

            for (int i = 0; i < 2000; i++) {
                User user = new User();
                user.setUserId(faker.internet().uuid().substring(0, 8)); // 유니크한 userId
                user.setPhoneNumber(faker.phoneNumber().cellPhone());
                user.setUserPw(faker.internet().password());
                user.setUserName(faker.name().fullName());
                user.setNickname(generateRandomNickname());
                user.setRegiDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                user.setMail(faker.internet().emailAddress());

                fakeUsers.add(user);
            }

            userRepository.saveAll(fakeUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void boardFakeData() {
        try {
            String directoryPath = "/Users/byeongyeongtae/Desktop/개";
            List<MultipartFile> files = getMultipartFiles(directoryPath);

            Faker faker = new Faker(new Locale("ko"));
            List<User> users = userRepository.findAll();
            List<Location> locations = new ArrayList<>();
            List<Breed> breeds = breedRepository.findAll();

            List<Board> boards = new ArrayList<>();
            List<Photo> photos = new ArrayList<>();

            Random random = new Random();

            for (int i = 0; i < 2000; i++) {
                Location location = new Location();
                location.setProvince(faker.address().state());
                location.setCity(faker.address().city());
                location.setDistrict(faker.address().streetName());

                locations.add(location);
            }
            locationRepository.saveAll(locations);


            for (int i = 0; i < Math.min(2000, files.size()); i++) {
                Board board = new Board();
                Photo photo = new Photo();

                User randomUser = users.get(random.nextInt(users.size()));
                Location randomLocation = locations.get(random.nextInt(locations.size()));
                Breed randomBreed = breeds.get(random.nextInt(breeds.size()));

                LocalDateTime postDate = faker.date()
                        .past(180, java.util.concurrent.TimeUnit.DAYS) // 180일 = 6개월
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                LocalDateTime lostDate = postDate.minusDays(random.nextInt(30) + 1);


                String[] emotions = {
                        "속상합니다", "너무 걱정됩니다", "애타게 찾고 있습니다",
                        "마음이 찢어집니다", "정말 안타깝습니다", "견딜 수가 없습니다"
                };
                String[] actions = {
                        "제발 연락 부탁드립니다", "도움이 절실합니다", "목격하시면 바로 알려주세요",
                        "신고 부탁드립니다", "꼭 찾아야 합니다", "소중한 제 강아지를 찾고 싶습니다"
                };
                String[] patterns = {
                        "강아지를 꼭 찾고 싶습니다. 사례금을 드리겠습니다. 제발 도와주세요.",
                        "소중한 가족을 잃어버렸습니다. 사례하겠습니다. 꼭 연락 부탁드립니다.",
                        "잃어버린 강아지를 찾습니다. 사례금 약속드리겠습니다. 작은 단서라도 연락 부탁드립니다.",
                        "강아지를 찾고 있습니다. 사례금을 준비했습니다. 목격하신 분은 꼭 연락주세요.",
                        "도와주신 분께 사례하겠습니다. 강아지가 꼭 무사히 돌아오길 간절히 바랍니다.",
                        "강아지를 잃어버렸습니다. 사례하겠습니다. 꼭 찾아주십시오.",
                        "잃어버린 강아지를 찾습니다. 도움 주시는 분께 사례하겠습니다.",
                        "강아지가 사라졌습니다. 사례하겠습니다. 단서가 있다면 꼭 연락주세요.",
                        "도와주신 분께 사례하겠습니다. 간절히 찾고 있습니다. 연락 부탁드립니다.",
                        "사례금을 약속드립니다. 강아지를 꼭 찾을 수 있도록 도와주세요."
                };

                String emotion = emotions[random.nextInt(emotions.length)];
                String action = actions[random.nextInt(actions.length)];
                String pattern = patterns[random.nextInt(patterns.length)];

                List<MultipartFile> tempFile = new ArrayList<>();
                tempFile.add(files.get(i));
                String filePath = "";
                for (MultipartFile multipartFile : tempFile) {
                    String originalFilename = multipartFile.getOriginalFilename();
                    String extension = originalFilename != null && originalFilename.contains(".") ?
                            originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
                    filePath = UUID.randomUUID().toString() + extension;
                    String path = "/Users/byeongyeongtae/uploads/" + filePath;
                    multipartFile.transferTo(new File(path));
                }

                String breed = AiService.getPrediction(tempFile);
                System.out.println(breed);

                board.setPostDate(postDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                board.setLostDate(lostDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                board.setBreed(breedRepository.findByBreedName(breed));
                board.setLocation(randomLocation);
                board.setUser(randomUser);
                board.setReport(0);
                board.setDogName(getRandomDogName());
                board.setDogAge(String.valueOf(random.nextInt(20) + 1));
                board.setDogGender(random.nextInt(1) + 1);
                board.setDogWeight(String.valueOf(random.nextInt(6) + 3));
                board.setMemo(String.format(emotion + " "+ action + " " + pattern));

                boards.add(board);

                photo.setBoard(board);
                photo.setFilePath(filePath);
                photo.setDeletedFlag(0);
                photo.setUploadDate(postDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                photos.add(photo);
            }

            boardRepository.saveAll(boards);
            photoRepository.saveAll(photos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomDogName() {
        Random random = new Random();
        return DOG_NAMES.get(random.nextInt(DOG_NAMES.size()));
    }

    private static final List<String> DOG_NAMES = List.of(
            "바둑이", "초코", "두부", "콩이", "뭉치", "구름", "까미", "복실이",
            "나비", "쫑이", "해피", "사랑", "몽이", "보리", "다롱이", "푸리",
            "참새", "토리", "라떼", "송이", "바비", "미미", "치치", "잭슨",
            "달이", "별이", "루나", "모카", "카롱", "구슬", "꼬미", "태양",
            "사탕", "단이", "자두", "까망", "하양", "블랙", "브라운", "오렌지",
            "참이", "두리", "찌니", "뚜뚜", "토토", "보보", "애니", "루비",
            "로이", "호두", "슈가", "앙꼬", "포도", "감자", "달콩", "도리",
            "호랑", "흰둥이", "검둥이", "도담", "복이", "찹쌀", "우유", "쿠키",
            "모모", "땅콩", "까꿍", "꼬꼬", "토리", "하늘", "바람", "산들",
            "밀크", "라라", "멜로", "로라", "쥬니", "루루", "보노", "블루",
            "소소", "꾸미", "아이", "별사탕", "유자", "체리", "빈이", "고미",
            "순이", "금이", "은이", "복덩이", "깜찍이", "졸리", "핑크", "화이트"
    );



    private static final List<String> ADJECTIVES = List.of(
            "쫄깃한", "바삭한", "촉촉한", "빠른", "느린", "강력한", "약한", "은밀한", "우아한", "멋진",
            "훌륭한", "화려한", "고요한", "잔잔한", "활기찬", "대담한", "용감한", "즐거운", "신비로운", "매혹적인",
            "화난", "기쁜", "슬픈", "놀라운", "재미있는", "행복한", "친절한", "까칠한", "따뜻한", "차가운",
            "축축한", "건조한", "시원한", "따끈한", "굳센", "유연한", "단단한", "부드러운", "선명한", "흐릿한",
            "사랑스러운", "미운", "커다란", "작은", "거대한", "아담한", "아름다운", "추한", "귀여운", "무서운",
            "심플한", "복잡한", "재빠른", "느긋한", "총명한", "어리석은", "똑똑한", "우둔한", "재치있는", "냉정한",
            "활달한", "소심한", "용의주도한", "덜렁대는", "애교있는", "시크한", "호탕한", "우아한", "적극적인", "소극적인",
            "달콤한", "씁쓸한", "짭짤한", "매운", "시원한", "따뜻한", "아픈", "건강한", "지친", "상쾌한",
            "풍부한", "가난한", "부유한", "깨끗한", "더러운", "빛나는", "어두운", "선한", "악한", "명랑한",
            "잔혹한", "열정적인", "침착한", "혼란스러운", "차분한", "강렬한", "격렬한", "섬세한", "분노한", "지혜로운",
            "자유로운", "위풍당당한", "조용한", "비밀스러운", "영리한", "비겁한", "단호한", "과감한", "능숙한", "천천히",
            "기막힌", "신속한", "화끈한", "독특한", "심오한", "상냥한", "도전적인", "자연스러운", "위대한", "감동적인"
    );

    private static final List<String> NOUNS = List.of(
            "망토", "사자", "호랑이", "늑대", "여우", "곰", "토끼", "다람쥐", "고양이", "강아지",
            "펭귄", "고래", "상어", "물개", "돌고래", "독수리", "참새", "비둘기", "앵무새", "공작",
            "독거미", "잠자리", "꿀벌", "나비", "반딧불이", "장수풍뎅이", "사슴벌레", "메뚜기", "개구리", "뱀",
            "용", "유니콘", "도깨비", "요정", "마녀", "도적", "기사", "왕", "여왕", "황제",
            "마법사", "신", "괴물", "귀신", "유령", "좀비", "뱀파이어", "괴도", "탐정", "요리사",
            "농부", "사냥꾼", "대장장이", "상인", "연금술사", "대장", "선장", "해적", "왕자", "공주",
            "광대", "화가", "음악가", "작곡가", "가수", "춤꾼", "연주자", "작가", "시인", "철학자",
            "과학자", "의사", "간호사", "마부", "어부", "장군", "영웅", "모험가", "탐험가", "수호자",
            "관리자", "감시자", "보호자", "수호천사", "지배자", "길잡이", "현자", "천재", "바보", "기인",
            "수호신", "천사", "악마", "탐구자", "개척자", "견습생", "달인", "연구원", "탐사자", "전사",
            "상상가", "창조자", "지휘자", "조각가", "재판관", "의장", "사서", "요술사", "방랑자", "경비병",
            "버섯", "나무", "폭풍", "해바라기", "장미", "카멜레온", "거북이", "코뿔소", "기린", "불사조",
            "전갈", "라쿤", "미어캣", "캥거루", "코알라", "수달", "불곰", "늑대인간", "정령", "고블린",
            "스핑크스", "드래곤", "황금사과", "다이아몬드", "사파이어", "에메랄드", "수정", "해골", "피리", "북",
            "검", "방패", "투구", "갑옷", "창", "활", "지팡이", "마법봉", "수정구슬", "부엉이",
            "낙타", "도마뱀", "두더지", "코끼리", "불새", "흑기사", "백기사", "돌", "모래시계", "불꽃"
    );

    public String generateRandomNickname() {
        Random random = new Random();

        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(random.nextInt(NOUNS.size()));
        int randomNumber = 1000 + random.nextInt(9000); // 1000~9999 범위의 숫자 생성

        return adjective + noun + randomNumber;
    }

    public static List<MultipartFile> getMultipartFiles(String directoryPath) throws IOException {
        List<MultipartFile> multipartFiles = new ArrayList<>();

        // 디렉토리에서 모든 파일 읽기
        File folder = new File(directoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("유효한 디렉토리가 아닙니다: " + directoryPath);
        }

        File[] files = folder.listFiles((dir, name) -> {
            // 이미지 파일만 필터링
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".jpg") || lowerName.endsWith(".png") || lowerName.endsWith(".jpeg");
        });

        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("이미지 파일이 없습니다: " + directoryPath);
        }

        // 파일을 MultipartFile로 변환
        for (File file : files) {
            try (FileInputStream input = new FileInputStream(file)) {
                MultipartFile multipartFile = new MockMultipartFile(
                        file.getName(),               // 파일 이름
                        file.getName(),               // 원본 파일 이름
                        "image/" + getExtension(file.getName()), // MIME 타입 추정
                        input                         // 파일 데이터
                );
                multipartFiles.add(multipartFile);
            }
        }

        return multipartFiles;
    }

    // 파일 확장자 추출 메서드
    private static String getExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        return lastIndexOfDot == -1 ? "" : fileName.substring(lastIndexOfDot + 1);
    }


}
