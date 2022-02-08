# WordPress_Puller
팀내 각각 보유 및 파편화 되어있는 테스트 데이터의 중앙집중화를 위해 Java Swing기반의 윈도우용 자바애플리케이션이다.</br>
서버는 기본적으로 워드프레스를 사용하며 Post단위로 테스트 데이터를 업로드하고 Rest API를 이용하여,</br>
Post의 첨부파일을 로컬디스크로 다운로드한다.</br>

사용법</br>
exe 를 실행한다.</br>
"Search" 버튼을 선택한다.</br>
워드프레스상에 있는 모든 카테고리 데이터가 include에 포함됨을 확인한다.</br>
만약 다운로드 제외할 카테고리가 있다면 항목을 더블클릭하여 exclude 로 이동시킨다.</br>
Browse 버튼을 선택해서 다운로드 될 디렉토리를 선택한다.</br>
"Pull" 버튼을 선택한 후 프로그레스바가 모두 100%로 변경될때까지 대기한다.</br>
다운로드 경로로 이동하여 데이터를 확인한다.</br>


기능 설명 </br>
![puller_main_view](https://user-images.githubusercontent.com/8287502/151955995-24b22cf5-49c2-4cb9-a29d-2d424601f1f8.png)

Path : EXE의 실행경로를 최초로 자동완성 된다.</br>
Browse : 다운로드 될 경로를 직접 선택하도록 경로선택기를 표시한다.</br>
Search & Pull : 워드프레스의 첨부파일(Post) 정보를 가져온다.</br>
                        첨부파일을 로컬디스크로 다운로드 한다.</br>
Stop : 이미 메모리에 올라가있는 워드프레스의 첨부파일 정보를 초기화 한다.</br>
include : 첨부파일을 다운로드 할 목록</br>
exclude : 다운로드 제외할 첨부파일 목록</br>
isDebug : 로그표시창에 자세한  디버깅로그 표시 (jar의 exception 확인이나 기타오류확인용도)</br></br>
Clear Log : 로그표시창의 내용 모두 제거
